package ucr.ac.cr.authentication.handlers.commands.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ucr.ac.cr.authentication.handlers.commands.RecoverPasswordHandler;
import ucr.ac.cr.authentication.handlers.queries.UserQuery;
import ucr.ac.cr.authentication.handlers.queries.impl.PasswordResetTokenQueryImpl;
import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.PasswordResetTokenRepository;
import ucr.ac.cr.authentication.models.PasswordRecoveryMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecoverPasswordHandlerImpl implements RecoverPasswordHandler {

    private final UserQuery userQuery;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenQueryImpl passwordResetTokenQuery;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecoverPasswordHandlerImpl(
            UserQuery userQuery, PasswordResetTokenRepository passwordResetTokenRepository, PasswordResetTokenQueryImpl passwordResetTokenQuery,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.userQuery = userQuery;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetTokenQuery = passwordResetTokenQuery;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result handle(Command command) {
        String email = command.email();

        Result emailValidation = validateEmail(email);
        if (!(emailValidation instanceof Result.Success)) return emailValidation;

        Optional<UserEntity> userOpt = userQuery.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new Result.UserNotFound("Usuario no encontrado.");
        }

        UserEntity user = userOpt.get();

        if (hasValidRecoveryToken(user)) {
            return new Result.AlreadyRequested("Ya existe una solicitud activa de recuperación.");
        }

        String token = generateToken();
        PasswordResetTokenEntity recoveryToken = createRecoveryToken(user, token);
        passwordResetTokenRepository.save(recoveryToken);
        boolean kafkaSuccess = sendKafkaMessage(email, token);

        if (!kafkaSuccess) {
            return new Result.EmailServiceError("Error al enviar el correo de recuperación.");
        }

        return new Result.Success();
    }

    private Result validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return new Result.InvalidEmail("El correo no puede estar vacío.");
        }

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return new Result.InvalidEmail("El correo no tiene un formato válido.");
        }

        return new Result.Success();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private PasswordResetTokenEntity createRecoveryToken(UserEntity user, String token) {
        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.setUser(user);
        entity.setToken(token);
        entity.setCreatedAt(LocalDateTime.now().withNano(0));
        entity.setExpiresAt(LocalDateTime.now().plusHours(1).withNano(0));
        return entity;
    }

    private boolean hasValidRecoveryToken(UserEntity user) {
        return passwordResetTokenQuery.findValidByUser(user).isPresent();
    }

    private boolean sendKafkaMessage(String email, String token) {
        try {
            PasswordRecoveryMessage msg = new PasswordRecoveryMessage(email, token);
            String jsonMsg = objectMapper.writeValueAsString(msg);
            kafkaTemplate.send("password-recovery", jsonMsg);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar correo de recuperación: " + e.getMessage());
            return false;
        }
    }

}