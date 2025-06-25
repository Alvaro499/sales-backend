package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateClientHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.ClientQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.ClientEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ClientRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreateClientHandlerImpl implements CreateClientHandler {

    private final ClientQuery clientQuery;
    private final ClientRepository clientRepo;

    public CreateClientHandlerImpl(ClientQuery clientQuery, ClientRepository clientRepo) {
        this.clientQuery = clientQuery;
        this.clientRepo = clientRepo;
    }

    @Override
    public UUID handle(Command command) {
        UUID clientId = command.clientId();

        // Si viene un ID y ya existe, simplemente lo devolvemos
        if (clientId != null && clientQuery.existsById(clientId)) {
            return clientId;
        }

        // Si vino un ID, pero no existe, NO lo usamos
        // Creamos uno nuevo con ID generado automáticamente
        //Se evita "Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)"
        ClientEntity newClient = new ClientEntity();
        newClient.setExpiresAt(LocalDateTime.now().plusDays(30));
        return clientRepo.save(newClient).getId();
    }
}