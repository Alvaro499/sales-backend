package ucr.ac.cr.BackendVentas.handlers.queries;


import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProductQuery {
    int getAvailableStock(UUID productId);

    Optional<ProductEntity> findById(UUID uuid);
    Optional<ProductEntity> findByName(String name);
}