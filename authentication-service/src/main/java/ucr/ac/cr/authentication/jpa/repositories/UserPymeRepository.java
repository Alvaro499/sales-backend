package ucr.ac.cr.authentication.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ucr.ac.cr.authentication.jpa.entities.UserPymeEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserPymeRepository extends JpaRepository<UserPymeEntity, UUID> {
    Optional<UserPymeEntity> findByUserId(UUID userId);
}
