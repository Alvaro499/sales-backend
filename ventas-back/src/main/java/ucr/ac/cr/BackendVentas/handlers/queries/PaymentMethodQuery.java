package ucr.ac.cr.BackendVentas.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;
import ucr.ac.cr.BackendVentas.jpa.entities.PaymentMethodEntity;

import java.util.Optional;

public interface PaymentMethodQuery {

    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
    Optional<PaymentMethodEntity> findByName(String name);
}
