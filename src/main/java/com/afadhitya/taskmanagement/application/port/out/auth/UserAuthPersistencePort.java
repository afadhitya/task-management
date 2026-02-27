package com.afadhitya.taskmanagement.application.port.out.auth;

import com.afadhitya.taskmanagement.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserAuthPersistencePort {

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    Optional<User> findByPasswordResetToken(String token);

    void updatePasswordResetToken(Long userId, String token, LocalDateTime expiresAt);

    void clearPasswordResetToken(Long userId);

    void updatePassword(Long userId, String newPasswordHash);
}
