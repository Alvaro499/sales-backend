package ucr.ac.cr.authentication.handlers.queries;

import ucr.ac.cr.authentication.jpa.entities.ConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmationCodeQuery {

    Optional<ConfirmationCodeEntity> findByCode(String code);

    Optional<ConfirmationCodeEntity> findValidByPyme(PymeEntity pyme);

    ConfirmationCodeEntity save(ConfirmationCodeEntity entity);
}
