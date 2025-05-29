package ucr.ac.cr.authentication.handlers.queries.impl;

import org.springframework.stereotype.Component;
import ucr.ac.cr.authentication.handlers.queries.UserRecoveryTokenQuery;
import ucr.ac.cr.authentication.jpa.entities.UserRecoveryTokenEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserRecoveryTokenRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRecoveryTokenQueryImpl implements UserRecoveryTokenQuery {

    private final UserRecoveryTokenRepository repository;

    public UserRecoveryTokenQueryImpl(UserRecoveryTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(UserRecoveryTokenEntity token) {
        repository.save(token);
    }

    @Override
    public Optional<UserRecoveryTokenEntity> findByToken(UUID token) {
        return repository.findByToken(token);
    }
}