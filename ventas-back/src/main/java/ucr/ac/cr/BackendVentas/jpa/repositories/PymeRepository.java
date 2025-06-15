package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

import java.util.Optional;
import java.util.UUID;

public interface PymeRepository extends JpaRepository <PymeEntity, String> {

    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Optional<PymeEntity> findById(UUID uuid);
    Optional<PymeEntity> findByEmail(String email);
    Optional<PymeEntity> findByUserId(UUID name);
}
