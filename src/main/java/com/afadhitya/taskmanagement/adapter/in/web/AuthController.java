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
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import com.afadhitya.taskmanagement.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        logoutUseCase.logout(userDetails.getId());
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
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponse response = getCurrentUserUseCase.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(response);
    }
}
