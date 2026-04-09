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
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "otp_code")
    private String otpCode;

    // This is the critical fix for your "Column not found" error
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    private int balance;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = 1000;
        this.isVerified = false; // New users must verify via 2FA
        this.accountNumber = "BNK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}