package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.ForgotPasswordRequest;
import com.afadhitya.taskmanagement.application.dto.request.LoginRequest;
import com.afadhitya.taskmanagement.application.dto.request.LogoutRequest;
import com.afadhitya.taskmanagement.application.dto.request.RefreshTokenRequest;
import com.afadhitya.taskmanagement.application.dto.request.RegisterRequest;
import com.afadhitya.taskmanagement.application.dto.request.ResetPasswordRequest;
import com.afadhitya.taskmanagement.application.dto.response.AuthResponse;
import com.afadhitya.taskmanagement.application.dto.response.UserResponse;
import com.afadhitya.taskmanagement.application.port.in.auth.ForgotPasswordUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.GetCurrentUserUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.LoginUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.LogoutUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.RefreshTokenUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.RegisterUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.ResetPasswordUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginUseCase.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        logoutUseCase.logout(request.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenUseCase.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.sendResetLink(request);
        // Always return 202 Accepted for security (don't reveal if email exists)
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestParam Long userId) {
        UserResponse response = getCurrentUserUseCase.getCurrentUser(userId);
        return ResponseEntity.ok(response);
    }
}
