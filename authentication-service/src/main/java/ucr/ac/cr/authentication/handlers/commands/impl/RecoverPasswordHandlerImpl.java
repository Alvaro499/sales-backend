package ucr.ac.cr.authentication.handlers.commands.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.service.EmailService;
import ucr.ac.cr.authentication.handlers.commands.RecoverPasswordHandler;
import ucr.ac.cr.authentication.handlers.queries.UserQuery;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.entities.UserRecoveryTokenEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserRecoveryTokenRepository;
import ucr.ac.cr.authentication.models.PasswordRecoveryMessage;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecoverPasswordHandlerImpl implements RecoverPasswordHandler {

    private final UserQuery userQuery;
    private final UserRecoveryTokenRepository userRecoveryTokenRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecoverPasswordHandlerImpl(
            UserQuery userQuery,
            UserRecoveryTokenRepository userRecoveryTokenRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.userQuery = userQuery;
        this.userRecoveryTokenRepository = userRecoveryTokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result handle(Command command) {
        String email = command.email();

        Result emailValidation = validateEmail(email);
        if (emailValidation != null) return emailValidation;

        Optional<UserEntity> userOpt = userQuery.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new Result.UserNotFound("Usuario no encontrado.");
        }

        UserEntity user = userOpt.get();
        String token = generateToken();

        UserRecoveryTokenEntity recoveryToken = createRecoveryToken(user, token);
        userRecoveryTokenRepository.save(recoveryToken);

        //Se usa try-catch porque se debe capturar un posible error del servicio de email
        // y de la busqueda del usuario, ya que no tenemos por el momento un GlobalException o algo
        try {
            PasswordRecoveryMessage msg = new PasswordRecoveryMessage(email, token);
            String jsonMsg = objectMapper.writeValueAsString(msg);
            kafkaTemplate.send("password-recovery", jsonMsg);
            return new Result.Success();
        } catch (Exception e) {
            System.err.println("Error al enviar evento Kafka: " + e.getMessage());
            return new Result.EmailServiceError("Error al procesar la recuperación.");
        }
    }

    private Result validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return new Result.InvalidEmail("El correo no puede estar vacío.");
        }
        if (!email.contains("@")) {
            return new Result.InvalidEmail("El formato del correo es inválido.");
        }
        return null;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private UserRecoveryTokenEntity createRecoveryToken(UserEntity user, String token) {
        UserRecoveryTokenEntity entity = new UserRecoveryTokenEntity();
        entity.setUser(user);
        entity.setToken(token);
        entity.setExpiresAt(LocalDateTime.now().plusHours(1));
        return entity;
    }
}