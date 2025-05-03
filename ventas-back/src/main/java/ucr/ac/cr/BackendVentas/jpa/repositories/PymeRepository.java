package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

public interface PymeRepository extends JpaRepository <PymeEntity, String> {

    boolean existsByEmail(String email);
}
