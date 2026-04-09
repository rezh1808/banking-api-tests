package com.bank;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Double amount;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(String username, String description, int amount) {
        this.username = username;
        this.description = description;
        this.amount = (double) amount;
        this.timestamp = java.time.LocalDateTime.now();
    }
}
