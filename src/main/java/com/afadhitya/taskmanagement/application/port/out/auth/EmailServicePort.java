package com.afadhitya.taskmanagement.application.port.out.auth;

public interface EmailServicePort {

    void sendPasswordResetEmail(String email, String resetToken, String resetUrl);
}
