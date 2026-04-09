package com.qa.controller;

import com.bank.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/bank")
public class AuthController {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    public AuthController(UserRepository userRepository,
                          TransactionRepository transactionRepository,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }
    private static final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String name = payload.get("username");
        String pass = payload.get("password");
        String email = payload.get("email");

        if (userRepository.existsById(name)) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User newUser = new User(name, pass, email);
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        newUser.setOtpCode(otp);
        userRepository.save(newUser);

        // SEND THE EMAIL
        try {
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok(Map.of("message", "OTP sent to " + email));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otpCode = request.get("otpCode");

        Optional<User> userOpt = userRepository.findById(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getOtpCode().equals(otpCode)) {
                user.setVerified(true);
                user.setOtpCode(null); // Clear the code after success
                userRepository.save(user);
                return ResponseEntity.ok("Verification Successful!");
            }
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP code.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String code = payload.get("otp");

        User user = userRepository.findById(username).orElse(null);

        if (user != null && user.getOtpCode().equals(code)) {
            user.setVerified(true);
            user.setOtpCode(null);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Account Verified!"));
        }
        return ResponseEntity.status(401).body("Invalid OTP Code");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam("username") String username) {
        try {
            User user = userRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newOtp = String.format("%06d", new Random().nextInt(999999));
            user.setOtpCode(newOtp);
            userRepository.save(user);

            emailService.sendOtpEmail(user.getEmail(), newOtp);

            // Log a successful event
            logger.info("New OTP sent successfully for user: {}", username);
            return ResponseEntity.ok("OTP Sent!");

        } catch (Exception e) {
            // Log the error with the full stack trace in the logs, not the console stream
            logger.error("Failed to resend OTP for user: {} | Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        User user = userRepository.findById(username).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            // CHECK IF VERIFIED
            if (!user.isVerified()) {
                return ResponseEntity.status(403).body("Account not verified. Please check your email.");
            }

            String token = UUID.randomUUID().toString();
            activeSessions.put(token, username);
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @GetMapping("/user-info")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = activeSessions.get(token);
        if (username == null) return ResponseEntity.status(403).build();

        // Fetch the latest data from the database
        return userRepository.findById(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestHeader("Authorization") String token,
                                      @RequestBody Map<String, Object> payload) {
        String username = activeSessions.get(token);
        if (username == null) return ResponseEntity.status(403).build();

        User sender = userRepository.findById(username).orElse(null);
        int amount = Integer.parseInt(payload.get("amount").toString());

        // We need to find the recipient by their Account Number
        String targetAccNumber = payload.get("targetAccount").toString();
        User recipient = userRepository.findByAccountNumber(targetAccNumber); // You'll need this method!

        if (sender == null || recipient == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Recipient account not found"));
        }

        if (sender.getBalance() < amount) {
            return ResponseEntity.badRequest().body(Map.of("message", "Insufficient funds"));
        }

        // 1. Subtract from Sender
        sender.setBalance(sender.getBalance() - amount);
        userRepository.save(sender);

        // 2. Add to Recipient
        recipient.setBalance(recipient.getBalance() + amount);
        userRepository.save(recipient);

        // 3. Record History for both (Optional but recommended)
        transactionRepository.save(new Transaction(username, "Sent to " + targetAccNumber, -amount));
        transactionRepository.save(new Transaction(recipient.getUsername(), "Received from " + sender.getUsername(), amount));

        return ResponseEntity.ok(Map.of("newBalance", sender.getBalance()));
    }

    // Stage 1: Request Reset
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        System.out.println("DEBUG: Reset request for email: " + email);
        // 1. Find the user (This uses the 'email' parameter!)
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // 2. Generate OTP (6-digit)
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));

        // 3. Save to DB
        user.setOtpCode(otp);
        userRepository.save(user);

        // 4. Send the signal (Email)
        emailService.sendEmail(user.getEmail(), "Password Reset OTP", "Your code is: " + otp);

        return ResponseEntity.ok("OTP sent to your email.");
    }

    // Stage 2: Confirm Reset
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetRequest request) {
        // 1. Find user by email from the request object
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Verify OTP (This uses the 'request' parameter!)
        if (user.getOtpCode() != null && user.getOtpCode().equals(request.getOtp())) {

            // 3. Update Password & Clear OTP
            user.setPassword(request.getNewPassword()); // In a real app, use passwordEncoder!
            user.setOtpCode(null);
            userRepository.save(user);

            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP.");
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(@RequestHeader("Authorization") String token) {
        String username = activeSessions.get(token);
        if (username == null) return ResponseEntity.status(403).build();

        User currentUser = userRepository.findById(username).orElse(null);
        if (currentUser == null) return ResponseEntity.status(404).build();

        return ResponseEntity.ok(currentUser.getBalance());
    }

    // --- TEST MOCK METHODS (Kept as is for your AccountTest/BaseTest) ---

    @PostMapping("/accounts")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody Map<String, Object> payload) {
        Object depositObj = payload.get("initialDeposit");
        int deposit = (depositObj instanceof Integer) ? (int) depositObj : 0;

        if (deposit < 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", "ACC123");
        response.put("name", payload.get("name"));
        response.put("balance", deposit);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(defaultValue = "1") int page) {
        Map<String, Object> response = new HashMap<>();
        List<String> userList = Arrays.asList("Reza", "Electrical Engineer Student");
        response.put("page", page);
        response.put("data", userList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logintest")
    public ResponseEntity<Map<String, Object>> logintest(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        if (!payload.containsKey("password") || payload.get("password").toString().isEmpty()) {
            response.put("message", "password required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!payload.get("password").equals("cityslicka")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        response.put("token", "QpwL5tke4Pnpja7X4");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}