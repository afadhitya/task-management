package com.afadhitya.taskmanagement.application.usecase.auth;

import com.afadhitya.taskmanagement.application.dto.request.ForgotPasswordRequest;
import com.afadhitya.taskmanagement.application.port.in.auth.ForgotPasswordUseCase;
import com.afadhitya.taskmanagement.application.port.out.auth.EmailServicePort;
import com.afadhitya.taskmanagement.application.port.out.auth.UserAuthPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordUseCaseImpl implements ForgotPasswordUseCase {

    private static final int TOKEN_LENGTH = 32;
    private static final int TOKEN_EXPIRY_HOURS = 1;

    private final UserAuthPersistencePort userAuthPersistencePort;
    private final EmailServicePort emailServicePort;

    @Value("${app.password-reset.url:http://localhost:3000/reset-password}")
    private String resetPasswordBaseUrl;

    @Override
    @Transactional
    public void sendResetLink(ForgotPasswordRequest request) {
        Optional<User> userOptional = userAuthPersistencePort.findByEmail(request.email());

        if (userOptional.isEmpty()) {
            // Silently return for security - don't reveal if email exists
            log.debug("Password reset requested for non-existent email: {}", request.email());
            return;
        }

        User user = userOptional.get();

        // Generate secure random token
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);

        // Save token to user
        userAuthPersistencePort.updatePasswordResetToken(user.getId(), token, expiresAt);

        // Send email (currently stubbed)
        String resetUrl = resetPasswordBaseUrl + "?token=" + token;
        emailServicePort.sendPasswordResetEmail(user.getEmail(), token, resetUrl);

        log.info("Password reset token generated for user: {}", user.getEmail());
    }

    private String generateSecureToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
