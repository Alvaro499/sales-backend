package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PymeConfirmationCodeRepository extends JpaRepository<PymeConfirmationCodeEntity, UUID> {
    Optional<PymeConfirmationCodeEntity> findFirstByPymeIdAndUsedFalse(UUID pymeId);
    Optional<PymeConfirmationCodeEntity> findTopByPymeIdOrderByCreatedAtDesc(UUID pymeId);

}
