package com.afadhitya.taskmanagement.application.port.in.user;

import com.afadhitya.taskmanagement.application.dto.response.UserResponse;

import java.util.List;

public interface GetAllUsersUseCase {

    List<UserResponse> getAllUsers();
}
