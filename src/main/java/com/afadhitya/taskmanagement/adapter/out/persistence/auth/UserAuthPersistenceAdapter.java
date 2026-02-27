package com.afadhitya.taskmanagement.adapter.out.persistence.auth;

import com.afadhitya.taskmanagement.adapter.out.persistence.UserRepository;
import com.afadhitya.taskmanagement.application.port.out.auth.UserAuthPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthPersistenceAdapter implements UserAuthPersistencePort {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token);
    }

    @Override
    public void updatePasswordResetToken(Long userId, String token, LocalDateTime expiresAt) {
        userRepository.updatePasswordResetToken(userId, token, expiresAt);
    }

    @Override
    public void clearPasswordResetToken(Long userId) {
        userRepository.clearPasswordResetToken(userId);
    }

    @Override
    public void updatePassword(Long userId, String newPasswordHash) {
        userRepository.updatePassword(userId, newPasswordHash);
    }
}
