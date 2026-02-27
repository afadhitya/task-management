package com.afadhitya.taskmanagement.application.usecase.auth;

import com.afadhitya.taskmanagement.application.dto.request.RegisterRequest;
import com.afadhitya.taskmanagement.application.dto.response.AuthResponse;
import com.afadhitya.taskmanagement.application.port.in.auth.RegisterUseCase;
import com.afadhitya.taskmanagement.application.port.out.auth.UserAuthPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterUseCaseImpl implements RegisterUseCase {

    private final UserAuthPersistencePort userAuthPersistencePort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userAuthPersistencePort.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .isActive(true)
                .build();

        User savedUser = userAuthPersistencePort.save(user);

        return new AuthResponse(
                null,
                "Bearer",
                null,
                new AuthResponse.UserInfo(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getFullName(),
                        savedUser.getAvatarUrl(),
                        savedUser.getCreatedAt()
                )
        );
    }
}
