package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.ShippingMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.ShippingMethodEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ShippingMethodRepository;

import java.util.Optional;

@Service
public class ShippingMethodQueryImpl implements ShippingMethodQuery {

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Override
    public boolean existsByNameIgnoreCaseAndIsActiveTrue(String name){
        return shippingMethodRepository.existsByNameIgnoreCaseAndIsActiveTrue(name);
    }

    @Override
    public Optional<ShippingMethodEntity> findByName(String name) {
        return shippingMethodRepository.findByName(name);
    }
}
