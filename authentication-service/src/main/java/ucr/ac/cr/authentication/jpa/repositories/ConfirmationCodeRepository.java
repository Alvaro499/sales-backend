package ucr.ac.cr.authentication.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.authentication.jpa.entities.ConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCodeEntity, UUID> {
    Optional<ConfirmationCodeEntity> findFirstByPymeAndUsedFalseAndExpiresAtAfter(PymeEntity pyme, LocalDateTime now);
    Optional<ConfirmationCodeEntity> findByCode(String code);
}
