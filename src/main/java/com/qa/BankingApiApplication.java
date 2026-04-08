package com.qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.bank")            // Tells Spring to find your @Entity User class
@EnableJpaRepositories("com.bank") // Tells Spring to find your UserRepository
public class BankingApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApiApplication.class, args);
    }
}