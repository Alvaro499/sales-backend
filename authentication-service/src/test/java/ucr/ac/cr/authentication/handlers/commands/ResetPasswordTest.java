package ucr.ac.cr.authentication.handlers.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import ucr.ac.cr.authentication.handlers.commands.impl.ResetPasswordHandlerImpl;
import ucr.ac.cr.authentication.handlers.queries.impl.PasswordResetTokenQueryImpl;
import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.PasswordResetTokenRepository;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ResetPasswordTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordResetTokenQueryImpl tokenQuery;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ResetPasswordHandlerImpl handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnInvalidTokenWhenTokenNotFound() {
        when(tokenQuery.findByToken("token")).thenReturn(Optional.empty());

        var result = handler.handle(new ResetPasswordHandler.Command("token", "newPassword123"));

        assertThat(result).isInstanceOf(ResetPasswordHandler.Result.InvalidToken.class);
        assertThat(((ResetPasswordHandler.Result.InvalidToken) result).message())
                .isEqualTo("Invalid token.");
    }

    @Test
    void shouldReturnInvalidTokenWhenTokenUsed() {
        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        tokenEntity.setUsed(true);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenQuery.findByToken("token")).thenReturn(Optional.of(tokenEntity));

        var result = handler.handle(new ResetPasswordHandler.Command("token", "newPassword123"));

        assertThat(result).isInstanceOf(ResetPasswordHandler.Result.InvalidToken.class);
        assertThat(((ResetPasswordHandler.Result.InvalidToken) result).message())
                .isEqualTo("El token ya ha sido utilizado");
    }

    @Test
    void shouldReturnInvalidTokenWhenTokenExpired() {
        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        tokenEntity.setUsed(false);
        tokenEntity.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(tokenQuery.findByToken("token")).thenReturn(Optional.of(tokenEntity));

        var result = handler.handle(new ResetPasswordHandler.Command("token", "newPassword123"));

        assertThat(result).isInstanceOf(ResetPasswordHandler.Result.InvalidToken.class);
        assertThat(((ResetPasswordHandler.Result.InvalidToken) result).message())
                .isEqualTo("El token ha expirado.");
    }

    @Test
    void shouldReturnInvalidPasswordWhenPasswordBlank() {
        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        tokenEntity.setUsed(false);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenQuery.findByToken("token")).thenReturn(Optional.of(tokenEntity));

        var result = handler.handle(new ResetPasswordHandler.Command("token", "  "));

        assertThat(result).isInstanceOf(ResetPasswordHandler.Result.InvalidPassword.class);
        assertThat(((ResetPasswordHandler.Result.InvalidPassword) result).message())
                .isEqualTo("Contraseña no puede estar vacía");
    }

    @Test
    void shouldReturnInvalidPasswordWhenPasswordTooShort() {
        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        tokenEntity.setUsed(false);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenQuery.findByToken("token")).thenReturn(Optional.of(tokenEntity));

        var result = handler.handle(new ResetPasswordHandler.Command("token", "short"));

        assertThat(result).isInstanceOf(ResetPasswordHandler.Result.InvalidPassword.class);
        assertThat(((ResetPasswordHandler.Result.InvalidPassword) result).message())
                .isEqualTo("La contraseña debe tener mínimo 8 carácteres");
    }

    @Test
    void shouldReturnUserNotFoundWhenUserNull() {
        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        tokenEntity.setUsed(false);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenEntity.setUser(null);

        when(tokenQuery.findByToken("token")).thenReturn(Optional.of(tokenEntity));

        var result = handler.handle(new ResetPasswordHandler.Command("token", "validPassword"));

        assertThat(result).isInstanceOf(ResetPasswordHandler.Result.UserNotFound.class);
        assertThat(((ResetPasswordHandler.Result.UserNotFound) result).message())
                .isEqualTo("Usuario no encontrado");
    }

    @Test
    void shouldUpdatePasswordAndReturnSuccess() {
        UserEntity user = new UserEntity();
        user.setPassword("oldPassword");

        PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
        tokenEntity.setUsed(false);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenEntity.setUser(user);

        when(tokenQuery.findByToken("token")).thenReturn(Optional.of(tokenEntity));
        when(passwordEncoder.encode("validPassword")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(tokenRepository.save(tokenEntity)).thenReturn(tokenEntity);

        var result = handler.handle(new ResetPasswordHandler.Command("token", "validPassword"));

        assertThat(result).isInstanceOf(ResetPasswordHandler.Result.Success.class);
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(tokenEntity.isUsed()).isTrue();

        verify(userRepository).save(user);
        verify(tokenRepository).save(tokenEntity);
    }

}
