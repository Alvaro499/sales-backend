package ucr.ac.cr.authentication.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.authentication.jpa.entities.UserRecoveryTokenEntity;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRecoveryTokenRepository extends JpaRepository<UserRecoveryTokenEntity, UUID> {
    Optional<UserRecoveryTokenEntity> findByToken(UUID token);
}