package com.bank;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically counts 1, 2, 3...
    private Long id;

    private String username;
    private String description;
    private int amount;
    private LocalDateTime timestamp;

    public Transaction() {} // Required by JPA

    public Transaction(String username, String description, int amount) {
        this.username = username;
        this.description = description;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
}