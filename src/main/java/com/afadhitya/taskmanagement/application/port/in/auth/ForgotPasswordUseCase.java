package com.afadhitya.taskmanagement.application.port.in.auth;

import com.afadhitya.taskmanagement.application.dto.request.ForgotPasswordRequest;

public interface ForgotPasswordUseCase {

    void sendResetLink(ForgotPasswordRequest request);
}
