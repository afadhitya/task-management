package com.afadhitya.taskmanagement.application.port.in.user;

import com.afadhitya.taskmanagement.application.dto.CreateUserRequest;
import com.afadhitya.taskmanagement.application.dto.UserResponse;

public interface CreateUserUseCase {

    UserResponse createUser(CreateUserRequest request);
}
