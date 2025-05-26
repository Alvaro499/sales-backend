package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PaymentMethodEntity;

import java.util.Optional;
import java.util.UUID;

public interface OrderLineRepository extends JpaRepository<OrderLineEntity, UUID> {

}
