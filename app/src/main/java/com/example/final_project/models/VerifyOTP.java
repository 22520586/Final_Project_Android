package com.example.final_project.models;

public class VerifyOTP {
    String email;
    String otp;
    public VerifyOTP(String email, String code) {
        this.email = email;
        this.otp = code;
    }

}
