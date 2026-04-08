package com.qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.qa", "com.bank"}) // This scans both folders for @RestController AND @Repository
@EntityScan("com.bank")
@EnableJpaRepositories("com.bank")
public class BankingApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApiApplication.class, args);
    }
}