package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
    Optional<ProductEntity> findById(UUID uuid);
    List<ProductEntity> findByPyme(PymeEntity pyme);
    Optional<ProductEntity> findByName(String name);
    long countByIsActiveTrue();

}
