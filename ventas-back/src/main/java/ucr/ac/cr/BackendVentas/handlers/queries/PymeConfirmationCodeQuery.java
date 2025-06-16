package ucr.ac.cr.BackendVentas.handlers.queries;

import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;

import java.util.Optional;
import java.util.UUID;

public interface PymeConfirmationCodeQuery {

    Optional<PymeConfirmationCodeEntity> findValidByPymeId(UUID pymeId);
    PymeConfirmationCodeEntity save(PymeConfirmationCodeEntity entity);

    Optional<PymeConfirmationCodeEntity> findLatestByPymeId(UUID pymeId);
}