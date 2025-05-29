package ucr.ac.cr.authentication.handlers.queries;

import ucr.ac.cr.authentication.jpa.entities.UserEntity;

import java.util.Optional;

public interface UserQuery {
    Optional<UserEntity> findByEmail(String email);
}