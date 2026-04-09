package com.bank;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetRequest {
    private String email;
    private String otp;
    private String newPassword;
}