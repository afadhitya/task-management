package com.afadhitya.taskmanagement.application.port.in.user;

import com.afadhitya.taskmanagement.application.dto.request.CreateUserRequest;
import com.afadhitya.taskmanagement.application.dto.response.UserResponse;

public interface CreateUserUseCase {

    UserResponse createUser(CreateUserRequest request);
}
