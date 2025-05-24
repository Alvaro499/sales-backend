package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.ShippingMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.repositories.ShippingMethodRepository;

@Service
public class ShippingMethodQueryImpl implements ShippingMethodQuery {

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Override
    public boolean existsByNameIgnoreCaseAndIsActiveTrue(String name){
        return shippingMethodRepository.existsByNameIgnoreCaseAndIsActiveTrue(name);
    }
}
