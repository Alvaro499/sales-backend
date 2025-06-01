package ucr.ac.cr.BackendVentas.handlers.queries;


import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;

import java.util.Optional;
import java.util.UUID;

public interface OrderLineQuery {

    Optional<OrderLineEntity> findById(UUID uuid);

    Optional<OrderLineEntity> save(OrderLineEntity line);
}