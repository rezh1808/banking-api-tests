package com.bank;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    private String username; // Username is now the "Primary Key"
    private String password;
    private int balance;
    private String accountNumber;

    // JPA needs a default constructor
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 1000;
        this.accountNumber = "BNK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}