package ucr.ac.cr.authentication.handlers.queries;

import ucr.ac.cr.authentication.jpa.entities.ConfirmationCodeEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ConfirmationCodeQuery {

    Optional<ConfirmationCodeEntity> findByCode(String code);

    Optional<ConfirmationCodeEntity> findValidByPymeId(UUID pymeId);

    ConfirmationCodeEntity save(ConfirmationCodeEntity entity);
}