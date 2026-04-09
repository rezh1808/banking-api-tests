package com.qa.controller;

import com.bank.Transaction;
import com.bank.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getHistory(@RequestParam String username) {
        // Fetches all transactions for the user, newest first
        List<Transaction> history = transactionRepository.findByUsernameOrderByTimestampDesc(username);
        return ResponseEntity.ok(history);
    }
}