package com.afadhitya.taskmanagement.application.usecase.user;

import com.afadhitya.taskmanagement.application.dto.CreateUserRequest;
import com.afadhitya.taskmanagement.application.dto.UserResponse;
import com.afadhitya.taskmanagement.application.mapper.UserMapper;
import com.afadhitya.taskmanagement.application.port.in.user.CreateUserUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userPersistencePort.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(request.password()); // In production, hash the password

        User savedUser = userPersistencePort.save(user);
        return userMapper.toResponse(savedUser);
    }
}
