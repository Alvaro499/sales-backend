package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.ProductQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ProductQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PaymentMethodRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductQueryImpl implements ProductQuery {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public int getAvailableStock(UUID productId) {
        Optional<ProductEntity> productOptional = productRepository.findById(productId);

        boolean productExists = productOptional.isPresent();
        if (productExists) {
            ProductEntity product = productOptional.get();
            int stock = product.getStock();
            return stock;
        } else {
            return 0;
        }
    }

    @Override
    public Optional<ProductEntity> findById(UUID uuid) {
        return productRepository.findById(uuid);
    }

    @Override
    public Optional<ProductEntity> findByName(String name) {
        return productRepository.findByName(name);
    }


}
