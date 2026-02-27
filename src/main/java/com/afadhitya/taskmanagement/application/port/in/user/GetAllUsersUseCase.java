package com.afadhitya.taskmanagement.application.port.in.user;

import com.afadhitya.taskmanagement.application.dto.UserResponse;

import java.util.List;

public interface GetAllUsersUseCase {

    List<UserResponse> getAllUsers();
}
