package com.afadhitya.taskmanagement.application.port.in.user;

import com.afadhitya.taskmanagement.application.dto.request.UpdateUserRequest;
import com.afadhitya.taskmanagement.application.dto.response.UserResponse;

public interface UpdateUserUseCase {

    UserResponse updateUser(Long id, UpdateUserRequest request);
}
