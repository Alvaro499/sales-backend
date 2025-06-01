package ucr.ac.cr.BackendVentas.handlers.queries;

import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;

import java.util.Optional;
import java.util.UUID;

@Service
public interface OrderQuery {
    Optional<OrderEntity> findById(UUID uuid);
    Optional<OrderEntity> save(OrderEntity order);
}