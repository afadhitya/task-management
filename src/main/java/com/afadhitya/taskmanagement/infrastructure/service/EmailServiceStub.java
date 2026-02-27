package com.afadhitya.taskmanagement.infrastructure.service;

import com.afadhitya.taskmanagement.application.port.out.auth.EmailServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Stub implementation of EmailServicePort for development/testing.
 * Logs email content instead of sending actual emails.
 * TODO: Replace with real implementation using JavaMailSender
 */
@Component
@Slf4j
public class EmailServiceStub implements EmailServicePort {

    @Override
    public void sendPasswordResetEmail(String email, String resetToken, String resetUrl) {
        log.info("========================================");
        log.info("PASSWORD RESET EMAIL (STUB)");
        log.info("========================================");
        log.info("To: {}", email);
        log.info("Reset Token: {}", resetToken);
        log.info("Reset URL: {}", resetUrl);
        log.info("========================================");
        log.info("Use the token above to test /auth/reset-password endpoint");
        log.info("========================================");
    }
}
