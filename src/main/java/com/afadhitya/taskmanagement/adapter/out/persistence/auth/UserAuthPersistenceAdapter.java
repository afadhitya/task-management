package com.afadhitya.taskmanagement.adapter.out.persistence.auth;

import com.afadhitya.taskmanagement.adapter.out.persistence.UserRepository;
import com.afadhitya.taskmanagement.application.port.out.auth.UserAuthPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthPersistenceAdapter implements UserAuthPersistencePort {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
