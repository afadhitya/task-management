package com.afadhitya.taskmanagement.application.usecase.user;

import com.afadhitya.taskmanagement.application.dto.request.UpdateUserRequest;
import com.afadhitya.taskmanagement.application.dto.response.UserResponse;
import com.afadhitya.taskmanagement.application.mapper.UserMapper;
import com.afadhitya.taskmanagement.application.port.in.user.UpdateUserUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userPersistencePort.existsByEmail(request.email())) {
                throw new IllegalArgumentException("Email already exists: " + request.email());
            }
        }

        userMapper.updateEntityFromRequest(request, user);

        if (request.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        User updatedUser = userPersistencePort.save(user);
        return userMapper.toResponse(updatedUser);
    }
}
