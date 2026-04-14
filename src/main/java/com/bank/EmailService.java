package com.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // This pulls your email from properties so it always matches your login
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Standard method to send any text email
     */
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    /**
     * Specific method for OTPs (Optional, but looks cleaner in your AuthController)
     */
    public void sendOtpEmail(String to, String otp) {
        String subject = "Your NyuboBank Verification Code";
        String body = "Halo!\n\nYour verification code is: " + otp +
                "\n\nIf you did not request this, please ignore this email.";
        sendEmail(to, subject, body);
    }
}