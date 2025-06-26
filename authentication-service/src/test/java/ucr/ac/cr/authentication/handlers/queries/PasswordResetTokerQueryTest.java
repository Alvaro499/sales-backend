package ucr.ac.cr.authentication.handlers.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ucr.ac.cr.authentication.handlers.queries.impl.PasswordResetTokenQueryImpl;
import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PasswordResetTokenQueryImplTest {

    private PasswordResetTokenRepository repository;
    private PasswordResetTokenQueryImpl query;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PasswordResetTokenRepository.class);
        query = new PasswordResetTokenQueryImpl(repository);
    }

    @Test
    void findByToken_returnsToken_whenExists() {
        String token = "abc123";
        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        when(repository.findByToken(token)).thenReturn(Optional.of(tokenEntity));

        Optional<PasswordResetTokenEntity> result = query.findByToken(token);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(tokenEntity);
        verify(repository, times(1)).findByToken(token);
    }

    @Test
    void findByToken_returnsEmpty_whenNotFound() {
        String token = "notfound";
        when(repository.findByToken(token)).thenReturn(Optional.empty());

        Optional<PasswordResetTokenEntity> result = query.findByToken(token);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findByToken(token);
    }

    @Test
    void findValidByUser_returnsToken_whenValidTokenExists() {
        UserEntity user = new UserEntity();
        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();

        when(repository.findFirstByUserAndUsedFalseAndExpiresAtAfter(
                eq(user), any(LocalDateTime.class)))
                .thenReturn(Optional.of(tokenEntity));

        Optional<PasswordResetTokenEntity> result = query.findValidByUser(user);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(tokenEntity);
        verify(repository, times(1))
                .findFirstByUserAndUsedFalseAndExpiresAtAfter(eq(user), any(LocalDateTime.class));
    }

    @Test
    void findValidByUser_returnsEmpty_whenNoValidToken() {
        UserEntity user = new UserEntity();

        when(repository.findFirstByUserAndUsedFalseAndExpiresAtAfter(
                eq(user), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Optional<PasswordResetTokenEntity> result = query.findValidByUser(user);

        assertThat(result).isEmpty();
        verify(repository, times(1))
                .findFirstByUserAndUsedFalseAndExpiresAtAfter(eq(user), any(LocalDateTime.class));
    }
}
