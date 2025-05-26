package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.BackendVentas.jpa.entities.PaymentMethodEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.ShippingMethodEntity;

import java.util.Optional;

@Repository
public interface ShippingMethodRepository  extends JpaRepository<ShippingMethodEntity, Integer> {
    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
    Optional<ShippingMethodEntity> findByName (String name);
}

