package ucr.ac.cr.authentication.handlers.commands.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ucr.ac.cr.authentication.client.basic.PymeClient;
import ucr.ac.cr.authentication.handlers.commands.ConfirmationCodeHandler;
import ucr.ac.cr.authentication.handlers.queries.ConfirmationCodeQuery;
import ucr.ac.cr.authentication.jpa.entities.ConfirmationCodeEntity;
import ucr.ac.cr.authentication.models.ConfirmationCodeMessage;
import ucr.ac.cr.authentication.models.PymeResponse;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class ConfirmationCodeHandlerImpl implements ConfirmationCodeHandler {

    private final ConfirmationCodeQuery codeQuery;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PymeClient pymeClient;

    public ConfirmationCodeHandlerImpl(
            ConfirmationCodeQuery codeQuery,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            PymeClient pymeClient
    ) {
        this.codeQuery = codeQuery;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.pymeClient = pymeClient;
    }

    @Override
    public Result handle(Command command) {
        String email = command.email();
        String inputCode = command.code();

        if (email == null || email.isBlank()) {
            return new Result.InvalidEmail("El correo está vacío.");
        }

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return new Result.InvalidEmail("El formato del correo no es válido.");
        }

        UUID pymeId = extractPymeIdFromEmail(email);
        if (pymeId == null) {
            return new Result.PymeNotFound("No se encontró ninguna pyme con ese correo.");
        }

        if (inputCode != null && !inputCode.isBlank()) {
            Optional<ConfirmationCodeEntity> opt = codeQuery.findByCode(inputCode);
            if (opt.isEmpty() || !opt.get().getPymeId().equals(pymeId)) {
                return new Result.InvalidCode("Código inválido.");
            }

            ConfirmationCodeEntity codeEntity = opt.get();
            if (codeEntity.isUsed()) {
                return new Result.InvalidCode("Código ya fue usado.");
            }

            if (codeEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                return new Result.CodeExpired("Código expirado.");
            }

            codeEntity.setUsed(true);
            codeQuery.save(codeEntity);

            try {
                pymeClient.activatePyme(pymeId);
            } catch (Exception e) {
                return new Result.EmailServiceError("Código verificado, pero no se pudo activar la pyme.");
            }

            return new Result.Verified();
        }

        if (codeQuery.findValidByPymeId(pymeId).isPresent()) {
            return new Result.AlreadyRequested("Ya se ha solicitado un código recientemente.");
        }

        String code = String.format("%04d", new Random().nextInt(10000));
        ConfirmationCodeEntity entity = new ConfirmationCodeEntity();
        entity.setPymeId(pymeId);
        entity.setCode(code);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        entity.setUsed(false);

        codeQuery.save(entity);

        if (sendKafkaMessage(email, code)) {
            return new Result.Success();
        } else {
            return new Result.EmailServiceError("No se pudo enviar el correo con el código.");
        }
    }

    private UUID extractPymeIdFromEmail(String email) {
        try {
            PymeResponse response = pymeClient.getByEmail(email);
            return response.id();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean sendKafkaMessage(String email, String code) {
        try {
            ConfirmationCodeMessage msg = new ConfirmationCodeMessage(email, code);
            String json = objectMapper.writeValueAsString(msg);
            kafkaTemplate.send("confirmation-code", json);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar código de confirmación: " + e.getMessage());
            return false;
        }
    }
}
