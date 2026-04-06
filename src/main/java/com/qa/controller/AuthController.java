package com.qa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bank") // Simplified to match your BaseTest + AccountTest paths
public class AuthController {

    // Matches .post("/accounts") in your test
    @PostMapping("/accounts")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody Map<String, Object> payload) {

        // 1. Get the deposit from the request body
        Object depositObj = payload.get("initialDeposit");
        int deposit = (depositObj instanceof Integer) ? (int) depositObj : 0;

        // 2. LOGIC FOR TC_Account_002 (Negative Deposit)
        // If deposit is negative, return 400 Bad Request
        if (deposit < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 3. LOGIC FOR TC_Account_001 (Success)
        Map<String, Object> response = new HashMap<>();
        response.put("accountId", "ACC123");
        response.put("name", payload.get("name"));
        response.put("balance", deposit);

        return new ResponseEntity<>(response, HttpStatus.CREATED); // Returns 201
    }

    // Matches .get("/users?page=2") in your test
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(defaultValue = "1") int page) {
        Map<String, Object> response = new HashMap<>();

        List<String> userList = new ArrayList<>();
        userList.add("Reza");
        userList.add("Electrical Engineer Student");

        response.put("page", page);
        response.put("data", userList);

        return new ResponseEntity<>(response, HttpStatus.OK); // Returns 200
    }
    // Matches .post("/login") OR .post("/auth/login")
    // depending on how you fix your test paths
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        // 1. Check for Missing Password (TC_Auth_002)
        if (!payload.containsKey("password") || payload.get("password").toString().isEmpty()) {
            response.put("message", "password required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
        }

        // 2. Check for Invalid Credentials (TC_Auth_003)
        // Mocking a check: if password isn't "cityslicka", fail it
        if (!payload.get("password").equals("cityslicka")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
        }

        // 3. Success (TC_Auth_001)
        response.put("token", "QpwL5tke4Pnpja7X4");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }
}