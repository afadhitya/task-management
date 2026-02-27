package com.afadhitya.taskmanagement.application.port.in.auth;

import com.afadhitya.taskmanagement.application.dto.response.UserResponse;

public interface GetCurrentUserUseCase {

    UserResponse getCurrentUser(Long userId);
}
