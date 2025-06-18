package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.PymeQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class PymeQueryImpl implements PymeQuery {

    private final PymeRepository pymeRepository;

    @Autowired
    public PymeQueryImpl(PymeRepository pymeRepository) {
        this.pymeRepository = pymeRepository;
    }

    @Override
    public Optional<PymeEntity> findById(UUID uuid) {
        return pymeRepository.findById(uuid);
    }

    @Override
    public Optional<PymeEntity> findByUserId(UUID userId) {
        return pymeRepository.findByUserId(userId);
    }
}