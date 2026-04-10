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

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    private int balance = 1000;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "otp_code")
    private String otpCode;

    public User() {}

    @PrePersist
    public void ensureAccountDetails() {
        if (this.accountNumber == null) {
            this.accountNumber = "BNK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    // You can keep this constructor for manual testing, but @PrePersist is the primary worker
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}