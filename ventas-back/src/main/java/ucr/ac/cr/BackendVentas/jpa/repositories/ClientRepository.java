package ucr.ac.cr.BackendVentas.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ucr.ac.cr.BackendVentas.jpa.entities.ClientEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findById(UUID id);

    // Eliminar todos los clientes con expiresAt antes de ahora (para el @Scheduled)
    void deleteByExpiresAtBefore(LocalDateTime time);

    boolean existsById(UUID id);
}