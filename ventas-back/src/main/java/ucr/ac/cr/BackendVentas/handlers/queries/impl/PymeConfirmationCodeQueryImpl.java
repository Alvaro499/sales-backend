package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.handlers.queries.PymeConfirmationCodeQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeConfirmationCodeRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class PymeConfirmationCodeQueryImpl implements PymeConfirmationCodeQuery {

    private final PymeConfirmationCodeRepository repository;

    public PymeConfirmationCodeQueryImpl(PymeConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PymeConfirmationCodeEntity> findValidByPymeId(UUID pymeId) {
        return repository.findFirstByPymeIdAndUsedFalse(pymeId);
    }

    @Override
    public PymeConfirmationCodeEntity save(PymeConfirmationCodeEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<PymeConfirmationCodeEntity> findLatestByPymeId(UUID pymeId) {
        return repository.findTopByPymeIdOrderByCreatedAtDesc(pymeId);
    }
}