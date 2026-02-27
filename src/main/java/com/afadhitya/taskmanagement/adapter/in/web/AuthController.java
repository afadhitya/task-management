package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.LoginRequest;
import com.afadhitya.taskmanagement.application.dto.request.LogoutRequest;
import com.afadhitya.taskmanagement.application.dto.request.RegisterRequest;
import com.afadhitya.taskmanagement.application.dto.response.AuthResponse;
import com.afadhitya.taskmanagement.application.port.in.auth.LoginUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.LogoutUseCase;
import com.afadhitya.taskmanagement.application.port.in.auth.RegisterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
