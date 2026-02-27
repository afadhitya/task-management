package com.afadhitya.taskmanagement.application.port.in.auth;

import com.afadhitya.taskmanagement.application.dto.request.ResetPasswordRequest;

public interface ResetPasswordUseCase {

    void resetPassword(ResetPasswordRequest request);
}
