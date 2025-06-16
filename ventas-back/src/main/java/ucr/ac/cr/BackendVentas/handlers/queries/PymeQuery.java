package ucr.ac.cr.BackendVentas.handlers.queries;

import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

import java.util.Optional;
import java.util.UUID;

public interface PymeQuery {

    Optional<PymeEntity> findById(UUID uuid);
    Optional<PymeEntity> findByUserId(UUID userId);
}
