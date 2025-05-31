package ucr.ac.cr.authentication.handlers.queries;

import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenQuery {

    Optional<PasswordResetTokenEntity> findByToken(String token);
    Optional<PasswordResetTokenEntity> findValidByUser(UserEntity user);
}