package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.PaymentMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.repositories.PaymentMethodRepository;

@Service
public class PaymentMethodQueryImpl implements PaymentMethodQuery {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public boolean existsByNameIgnoreCaseAndIsActiveTrue(String name) {
        return paymentMethodRepository.existsByNameIgnoreCaseAndIsActiveTrue(name);
    }
}
