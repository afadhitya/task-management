package com.afadhitya.taskmanagement.application.port.in.auth;

import com.afadhitya.taskmanagement.application.dto.request.LoginRequest;
import com.afadhitya.taskmanagement.application.dto.response.AuthResponse;

public interface LoginUseCase {

    AuthResponse login(LoginRequest request);
}
