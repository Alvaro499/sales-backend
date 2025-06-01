package ucr.ac.cr.BackendVentas.handlers.queries;

import ucr.ac.cr.BackendVentas.jpa.entities.ShippingMethodEntity;

import java.util.Optional;

public interface ShippingMethodQuery {

    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
    Optional<ShippingMethodEntity> findByName (String name);
}
