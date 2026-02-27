package com.afadhitya.taskmanagement.application.usecase.auth;

import com.afadhitya.taskmanagement.application.dto.request.ResetPasswordRequest;
import com.afadhitya.taskmanagement.application.port.in.auth.ResetPasswordUseCase;
import com.afadhitya.taskmanagement.application.port.out.auth.UserAuthPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private final UserAuthPersistencePort userAuthPersistencePort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userAuthPersistencePort.findByPasswordResetToken(request.token())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));

        // Verify email matches the token's user
        if (!user.getEmail().equalsIgnoreCase(request.email())) {
            throw new InvalidTokenException("Invalid or expired reset token");
        }

        // Check if token is expired
        if (user.getPasswordResetTokenExpiresAt() == null ||
                user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Reset token has expired");
        }

        // Hash new password and update
        String newPasswordHash = passwordEncoder.encode(request.newPassword());
        userAuthPersistencePort.updatePassword(user.getId(), newPasswordHash);

        // Clear the reset token
        userAuthPersistencePort.clearPasswordResetToken(user.getId());

        // TODO: Optionally invalidate all refresh tokens for security

        log.info("Password reset successfully for user: {}", user.getEmail());
    }
}
