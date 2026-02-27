package com.afadhitya.taskmanagement.application.port.in.user;

import com.afadhitya.taskmanagement.application.dto.UpdateUserRequest;
import com.afadhitya.taskmanagement.application.dto.UserResponse;

public interface UpdateUserUseCase {

    UserResponse updateUser(Long id, UpdateUserRequest request);
}
