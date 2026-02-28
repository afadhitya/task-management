package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByPasswordResetToken(String passwordResetToken);

    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = :token, u.passwordResetTokenExpiresAt = :expiresAt WHERE u.id = :userId")
    void updatePasswordResetToken(@Param("userId") Long userId,
                                  @Param("token") String token,
                                  @Param("expiresAt") LocalDateTime expiresAt);

    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = null, u.passwordResetTokenExpiresAt = null WHERE u.id = :userId")
    void clearPasswordResetToken(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User u SET u.passwordHash = :newPasswordHash WHERE u.id = :userId")
    void updatePassword(@Param("userId") Long userId, @Param("newPasswordHash") String newPasswordHash);

    @Query("""
            SELECT u FROM User u
            JOIN WorkspaceMember wm ON wm.user.id = u.id
            WHERE wm.workspace.id = :workspaceId
            AND (:query IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                 OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')))
            """)
    List<User> searchByWorkspaceId(@Param("workspaceId") Long workspaceId, @Param("query") String query);
}
