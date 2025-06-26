package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.ClientQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.ClientEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ClientRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientQueryImpl implements ClientQuery {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Optional<ClientEntity> findById(UUID id) {
        return clientRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return clientRepository.existsById(id);
    }

}
