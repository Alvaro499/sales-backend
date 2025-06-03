package ucr.ac.cr.authentication.handlers.commands.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ucr.ac.cr.authentication.handlers.commands.ConfirmationCodeHandler;
import ucr.ac.cr.authentication.jpa.entities.ConfirmationCodeEntity;
import ucr.ac.cr.authentication.jpa.repositories.ConfirmationCodeRepository;
import ucr.ac.cr.authentication.models.ConfirmationCodeMessage;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ConfirmationCodeHandlerImpl implements ConfirmationCodeHandler {

    private final PymeRepository pymeRepository;
    private final ConfirmationCodeRepository codeRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ConfirmationCodeHandlerImpl(
            PymeRepository pymeRepository,
            ConfirmationCodeRepository codeRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.pymeRepository = pymeRepository;
        this.codeRepository = codeRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
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

        if (!pymeRepository.existsByEmail(email)) {
            return new Result.PymeNotFound("No se encontró ninguna pyme con ese correo.");
        }

        PymeEntity pyme = pymeRepository.findAll().stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (pyme == null) {
            return new Result.PymeNotFound("Error: la pyme no se pudo recuperar.");
        }

        if (inputCode != null && !inputCode.isBlank()) {
            Optional<ConfirmationCodeEntity> opt = codeRepository.findByCode(inputCode);
            if (opt.isEmpty() || !opt.get().getPyme().equals(pyme)) {
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
            pyme.setActive(true);
            codeRepository.save(codeEntity);
            pymeRepository.save(pyme);

            return new Result.Verified();
        }

        boolean exists = codeRepository
                .findFirstByPymeAndUsedFalseAndExpiresAtAfter(pyme, LocalDateTime.now())
                .isPresent();
        if (exists) {
            return new Result.AlreadyRequested("Se ha solicitado un código recientemente.");
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        ConfirmationCodeEntity entity = new ConfirmationCodeEntity();
        entity.setPyme(pyme);
        entity.setCode(code);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        entity.setUsed(false);
        codeRepository.save(entity);

        try {
            ConfirmationCodeMessage msg = new ConfirmationCodeMessage(email, code);
            String json = objectMapper.writeValueAsString(msg);
            kafkaTemplate.send("confirmation-code", json);
            return new Result.Success();
        } catch (Exception e) {
            return new Result.EmailServiceError("No se pudo enviar el correo con el código.");
        }
    }
}