package ucr.ac.cr.authentication.handlers.queries.impl;

import org.springframework.stereotype.Component;
import ucr.ac.cr.authentication.handlers.queries.PasswordResetTokenQuery;
import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PasswordResetTokenQueryImpl implements PasswordResetTokenQuery {

    private final PasswordResetTokenRepository repository;

    public PasswordResetTokenQueryImpl(PasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PasswordResetTokenEntity> findByToken(String token) {

        return repository.findByToken(token);
    }

    @Override
    public Optional<PasswordResetTokenEntity> findValidByUser(UserEntity user) {
        return repository.findFirstByUserAndUsedFalseAndExpiresAtAfter(user, LocalDateTime.now());
    }

}