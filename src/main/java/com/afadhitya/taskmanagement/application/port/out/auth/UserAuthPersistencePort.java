package com.afadhitya.taskmanagement.application.port.out.auth;

import com.afadhitya.taskmanagement.domain.entity.User;

import java.util.Optional;

public interface UserAuthPersistencePort {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
