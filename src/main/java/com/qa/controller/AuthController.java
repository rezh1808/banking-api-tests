package com.qa.controller;

import com.bank.Transaction;
import com.bank.TransactionRepository;
import com.bank.User;
import com.bank.UserRepository;
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

    @Autowired
    public AuthController(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }
    private static final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> payload) {
        String name = payload.get("username");
        String pass = payload.get("password");

        // Use the Repository to check the database
        if (userRepository.existsById(name)) {
            return ResponseEntity.badRequest().body("User exists");
        }

        // Save the new User object to the database
        userRepository.save(new User(name, pass));
        return ResponseEntity.ok("Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        User user = userRepository.findById(username).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            activeSessions.put(token, username);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
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

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getHistory(@RequestHeader("Authorization") String token) {
        String username = activeSessions.get(token);
        if (username == null) return ResponseEntity.status(403).build();

        List<Transaction> history = transactionRepository.findByUsernameOrderByTimestampDesc(username);
        return ResponseEntity.ok(history);
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