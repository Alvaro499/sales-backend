package ucr.ac.cr.BackendVentas.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;

public interface PaymentMethodQuery {


    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
}
