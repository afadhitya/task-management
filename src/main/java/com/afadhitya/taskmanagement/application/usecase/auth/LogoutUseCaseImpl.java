package com.afadhitya.taskmanagement.application.usecase.auth;

import com.afadhitya.taskmanagement.application.port.in.auth.LogoutUseCase;
import com.afadhitya.taskmanagement.application.port.out.auth.UserAuthPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final UserAuthPersistencePort userAuthPersistencePort;

    @Override
    @Transactional
    public void logout(Long userId) {
        User user = userAuthPersistencePort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Clear the refresh token to invalidate it
        user.setRefreshToken(null);
        userAuthPersistencePort.save(user);
    }
}
