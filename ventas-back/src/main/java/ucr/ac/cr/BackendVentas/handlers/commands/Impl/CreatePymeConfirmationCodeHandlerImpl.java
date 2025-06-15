package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.commands.CreatePymeConfirmationCodeHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeConfirmationCodeRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class CreatePymeConfirmationCodeHandlerImpl implements CreatePymeConfirmationCodeHandler {

    private final PymeConfirmationCodeRepository repository;

    public CreatePymeConfirmationCodeHandlerImpl(PymeConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public String handle(PymeEntity pyme) {
        String code = generateCode();

        PymeConfirmationCodeEntity entity = new PymeConfirmationCodeEntity();
        entity.setPyme(pyme);
        entity.setCode(code);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUsed(false);

        repository.save(entity);
        return code;
    }

    /**
     * Genera un código de verificación de 4 dígitos.
     * La lógica de generación utiliza un número aleatorio entre 0 y 9999,
     * que luego se formatea a 4 dígitos con ceros a la izquierda.
     * Ejemplo si el número aleatorio es 5, el código generado será "0005".
     */
    private String generateCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }
}