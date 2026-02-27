package com.afadhitya.taskmanagement.application.usecase.user;

import com.afadhitya.taskmanagement.application.port.in.user.DeleteUserByIdUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteUserByIdUseCaseImpl implements DeleteUserByIdUseCase {

    private final UserPersistencePort userPersistencePort;

    @Override
    public void deleteUser(Long id) {
        if (!userPersistencePort.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userPersistencePort.deleteById(id);
    }
}
