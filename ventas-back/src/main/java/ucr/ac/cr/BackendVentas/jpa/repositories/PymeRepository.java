package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.BackendVentas.jpa.entities.Pyme;

public interface PymeRepository extends JpaRepository <Pyme, String> {

}
