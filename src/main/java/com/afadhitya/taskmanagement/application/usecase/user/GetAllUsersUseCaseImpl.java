package com.afadhitya.taskmanagement.application.usecase.user;

import com.afadhitya.taskmanagement.application.dto.UserResponse;
import com.afadhitya.taskmanagement.application.mapper.UserMapper;
import com.afadhitya.taskmanagement.application.port.in.user.GetAllUsersUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllUsersUseCaseImpl implements GetAllUsersUseCase {

    private final UserPersistencePort userPersistencePort;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        return userPersistencePort.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
