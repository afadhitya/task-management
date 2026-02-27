package com.afadhitya.taskmanagement.application.port.in.user;

import com.afadhitya.taskmanagement.application.dto.UserResponse;

public interface GetUserByIdUseCase {

    UserResponse getUserById(Long id);
}
