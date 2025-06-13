package ucr.ac.cr.authentication.handlers.queries.impl;

import org.springframework.stereotype.Component;
import ucr.ac.cr.authentication.handlers.queries.ConfirmationCodeQuery;
import ucr.ac.cr.authentication.jpa.entities.ConfirmationCodeEntity;
import ucr.ac.cr.authentication.jpa.repositories.ConfirmationCodeRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConfirmationCodeQueryImpl implements ConfirmationCodeQuery {

    private final ConfirmationCodeRepository repository;

    public ConfirmationCodeQueryImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ConfirmationCodeEntity> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public Optional<ConfirmationCodeEntity> findValidByPymeId(UUID pymeId) {
        return repository.findFirstByPymeIdAndUsedFalseAndExpiresAtAfter(pymeId, LocalDateTime.now());
    }

    @Override
    public ConfirmationCodeEntity save(ConfirmationCodeEntity entity) {
        return repository.save(entity);
    }
}