package com.afadhitya.taskmanagement.application.usecase.user;

import com.afadhitya.taskmanagement.application.dto.UserResponse;
import com.afadhitya.taskmanagement.application.mapper.UserMapper;
import com.afadhitya.taskmanagement.application.port.in.user.GetUserByIdUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserByIdUseCaseImpl implements GetUserByIdUseCase {

    private final UserPersistencePort userPersistencePort;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserById(Long id) {
        User user = userPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }
}
