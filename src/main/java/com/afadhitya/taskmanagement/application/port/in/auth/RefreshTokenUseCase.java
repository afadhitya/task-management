package com.afadhitya.taskmanagement.application.port.in.auth;

import com.afadhitya.taskmanagement.application.dto.request.RefreshTokenRequest;
import com.afadhitya.taskmanagement.application.dto.response.AuthResponse;

public interface RefreshTokenUseCase {

    AuthResponse refreshToken(RefreshTokenRequest request);
}
