package com.afadhitya.taskmanagement.application.usecase.auth;

import com.afadhitya.taskmanagement.application.dto.response.UserResponse;
import com.afadhitya.taskmanagement.application.mapper.UserMapper;
import com.afadhitya.taskmanagement.application.port.in.auth.GetCurrentUserUseCase;
import com.afadhitya.taskmanagement.application.port.out.auth.UserAuthPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase {

    private final UserAuthPersistencePort userAuthPersistencePort;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = userAuthPersistencePort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapper.toResponse(user);
    }
}
