package com.afadhitya.taskmanagement.application.port.in.auth;

import com.afadhitya.taskmanagement.application.dto.request.RegisterRequest;
import com.afadhitya.taskmanagement.application.dto.response.AuthResponse;

public interface RegisterUseCase {

    AuthResponse register(RegisterRequest request);
}
