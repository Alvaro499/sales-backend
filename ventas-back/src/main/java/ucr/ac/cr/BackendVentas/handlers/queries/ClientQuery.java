package ucr.ac.cr.BackendVentas.handlers.queries;

import ucr.ac.cr.BackendVentas.jpa.entities.ClientEntity;

import java.util.Optional;
import java.util.UUID;

public interface ClientQuery {

    Optional<ClientEntity> findById(UUID id);

    boolean existsById(UUID id);
}