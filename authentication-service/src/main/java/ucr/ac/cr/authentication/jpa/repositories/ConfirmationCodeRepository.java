package ucr.ac.cr.authentication.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.authentication.jpa.entities.ConfirmationCodeEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCodeEntity, UUID> {
    Optional<ConfirmationCodeEntity> findFirstByPymeIdAndUsedFalseAndExpiresAtAfter(UUID pymeId, LocalDateTime now);
    Optional<ConfirmationCodeEntity> findByCode(String code);
}
