package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.CreateUserRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateUserRequest;
import com.afadhitya.taskmanagement.application.dto.response.UserResponse;
import com.afadhitya.taskmanagement.application.port.in.user.CreateUserUseCase;
import com.afadhitya.taskmanagement.application.port.in.user.DeleteUserByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.user.GetAllUsersUseCase;
import com.afadhitya.taskmanagement.application.port.in.user.GetUserByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.user.UpdateUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserByIdUseCase deleteUserByIdUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = createUserUseCase.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = getAllUsersUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = getUserByIdUseCase.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = updateUserUseCase.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserByIdUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
