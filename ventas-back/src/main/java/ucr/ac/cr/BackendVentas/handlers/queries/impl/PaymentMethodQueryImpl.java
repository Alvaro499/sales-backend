package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.PaymentMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.PaymentMethodEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PaymentMethodRepository;

import java.util.Optional;

@Service
public class PaymentMethodQueryImpl implements PaymentMethodQuery {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public boolean existsByNameIgnoreCaseAndIsActiveTrue(String name) {
        return paymentMethodRepository.existsByNameIgnoreCaseAndIsActiveTrue(name);
    }

    @Override
    public Optional<PaymentMethodEntity> findByName(String name) {
        return paymentMethodRepository.findByName(name);
    }
}
