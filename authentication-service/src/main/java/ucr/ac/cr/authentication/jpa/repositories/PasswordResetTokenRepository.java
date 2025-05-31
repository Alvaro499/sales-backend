package ucr.ac.cr.authentication.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    Optional<PasswordResetTokenEntity> findByToken(String token);
    Optional<PasswordResetTokenEntity> findFirstByUserAndUsedFalseAndExpiresAtAfter(UserEntity user, LocalDateTime now);
}