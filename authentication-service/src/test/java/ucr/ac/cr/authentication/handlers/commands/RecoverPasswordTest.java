package ucr.ac.cr.authentication.handlers.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import ucr.ac.cr.authentication.handlers.commands.RecoverPasswordHandler;
import ucr.ac.cr.authentication.handlers.commands.impl.RecoverPasswordHandlerImpl;
import ucr.ac.cr.authentication.handlers.queries.UserQuery;
import ucr.ac.cr.authentication.handlers.queries.impl.PasswordResetTokenQueryImpl;
import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.PasswordResetTokenRepository;
import ucr.ac.cr.authentication.models.PasswordRecoveryMessage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecoverPasswordTest {

    @Mock
    private UserQuery userQuery;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordResetTokenQueryImpl tokenQuery;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecoverPasswordHandlerImpl handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- INVALID EMAIL CASES ----------

    @Test
    void shouldReturnInvalidEmailWhenEmailIsNull() {
        // Arrange
        RecoverPasswordHandler.Command command = new RecoverPasswordHandler.Command(null);

        // Act
        RecoverPasswordHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(RecoverPasswordHandler.Result.InvalidEmail.class, result);
    }

    @Test
    void shouldReturnInvalidEmailWhenEmailIsBlank() {
        RecoverPasswordHandler.Command command = new RecoverPasswordHandler.Command("  ");
        RecoverPasswordHandler.Result result = handler.handle(command);
        assertInstanceOf(RecoverPasswordHandler.Result.InvalidEmail.class, result);
    }

    @Test
    void shouldReturnInvalidEmailWhenEmailHasWrongFormat() {
        RecoverPasswordHandler.Command command = new RecoverPasswordHandler.Command("bad-email.com");
        RecoverPasswordHandler.Result result = handler.handle(command);
        assertInstanceOf(RecoverPasswordHandler.Result.InvalidEmail.class, result);
    }

    // ---------- USER NOT FOUND ----------

    @Test
    void shouldReturnUserNotFoundWhenEmailIsValidButUserDoesNotExist() {
        String email = "test@email.com";
        when(userQuery.findByEmail(email)).thenReturn(Optional.empty());

        RecoverPasswordHandler.Command command = new RecoverPasswordHandler.Command(email);
        RecoverPasswordHandler.Result result = handler.handle(command);

        assertInstanceOf(RecoverPasswordHandler.Result.UserNotFound.class, result);
    }

    // ---------- ALREADY REQUESTED ----------

    @Test
    void shouldReturnAlreadyRequestedWhenUserHasActiveToken() {
        String email = "test@email.com";
        UserEntity user = new UserEntity();
        when(userQuery.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenQuery.findValidByUser(user)).thenReturn(Optional.of(new PasswordResetTokenEntity()));

        RecoverPasswordHandler.Command command = new RecoverPasswordHandler.Command(email);
        RecoverPasswordHandler.Result result = handler.handle(command);

        assertInstanceOf(RecoverPasswordHandler.Result.AlreadyRequested.class, result);
    }

    // ---------- KAFKA FAILURE ----------

    @Test
    void shouldReturnEmailServiceErrorWhenKafkaFails() throws Exception {
        String email = "test@email.com";
        UserEntity user = new UserEntity();
        when(userQuery.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenQuery.findValidByUser(user)).thenReturn(Optional.empty());

        // Kafka falla por exception al convertir JSON
        when(objectMapper.writeValueAsString(any(PasswordRecoveryMessage.class)))
                .thenThrow(new RuntimeException("Kafka Error"));

        RecoverPasswordHandler.Command command = new RecoverPasswordHandler.Command(email);
        RecoverPasswordHandler.Result result = handler.handle(command);

        assertInstanceOf(RecoverPasswordHandler.Result.EmailServiceError.class, result);
    }

    // ---------- SUCCESS ----------

    @Test
    void shouldReturnSuccessWhenEmailIsValidAndEverythingGoesWell() throws Exception {
        String email = "test@email.com";
        UserEntity user = new UserEntity();
        when(userQuery.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenQuery.findValidByUser(user)).thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(any(PasswordRecoveryMessage.class))).thenReturn("{}");

        RecoverPasswordHandler.Command command = new RecoverPasswordHandler.Command(email);
        RecoverPasswordHandler.Result result = handler.handle(command);

        assertInstanceOf(RecoverPasswordHandler.Result.Success.class, result);
        verify(tokenRepository).save(any(PasswordResetTokenEntity.class));
        verify(kafkaTemplate).send(eq("password-recovery"), eq("{}"));
    }
}
