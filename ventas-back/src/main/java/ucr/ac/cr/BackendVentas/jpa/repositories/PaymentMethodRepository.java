package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.BackendVentas.jpa.entities.PaymentMethodEntity;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Integer> {
    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
}
