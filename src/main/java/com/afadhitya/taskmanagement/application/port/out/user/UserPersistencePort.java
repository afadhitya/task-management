package com.afadhitya.taskmanagement.application.port.out.user;

import com.afadhitya.taskmanagement.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Output port for user persistence operations.
 * This interface is implemented by the persistence adapter.
 */
public interface UserPersistencePort {

    User save(User user);

    Optional<User> findById(Long id);
}
