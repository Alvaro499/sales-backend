package ucr.ac.cr.authentication.handlers.queries;

import ucr.ac.cr.authentication.jpa.entities.UserRecoveryTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRecoveryTokenQuery {
    void save(UserRecoveryTokenEntity token);

    Optional<UserRecoveryTokenEntity> findByToken(String token);

}