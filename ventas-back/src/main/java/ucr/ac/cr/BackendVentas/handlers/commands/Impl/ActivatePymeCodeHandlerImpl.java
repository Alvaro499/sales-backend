package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.commands.ActivatePymeCodeHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.PymeConfirmationCodeQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.PymeQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.impl.PymeQueryImpl;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeConfirmationCodeRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class ActivatePymeCodeHandlerImpl implements ActivatePymeCodeHandler {

    private final PymeQuery pymeQuery;
    private final PymeRepository pymeRepository;
    private final PymeConfirmationCodeRepository codeRepository;
    private final PymeConfirmationCodeQuery codeQuery;

    public ActivatePymeCodeHandlerImpl(PymeQueryImpl pymeQuery,
                                       PymeRepository pymeRepository,
                                       PymeConfirmationCodeRepository codeRepository,
                                       PymeConfirmationCodeQuery codeQuery) {
        this.pymeQuery = pymeQuery;
        this.pymeRepository = pymeRepository;
        this.codeRepository = codeRepository;
        this.codeQuery = codeQuery;
    }

    @Override
    public Result handle(Command command) {

        Optional<PymeEntity> pymeOpt = getCurrentPyme(command.userID());
        if (pymeOpt.isEmpty()) {
            return new Result.InvalidCode("No se encontró la pyme del usuario.");
        }

        PymeEntity pyme = pymeOpt.get();
        Optional<PymeConfirmationCodeEntity> codeOpt = findConfirmationCode(pyme.getId());

        if (codeOpt.isEmpty()) {
            return new Result.InvalidCode("Código incorrecto o expirado.");
        }

        return processCodeValidation(codeOpt.get(), pyme, command.code());
    }

    private Optional<PymeEntity> getCurrentPyme(UUID userId) {
        return pymeQuery.findByUserId(userId);
    }

    private Optional<PymeConfirmationCodeEntity> findConfirmationCode(UUID pymeId) {
        return codeQuery.findLatestByPymeId(pymeId);
    }

    private Result processCodeValidation(PymeConfirmationCodeEntity codeEntity, PymeEntity pyme, String inputCode) {

        if (inputCode == null || inputCode.isBlank()) {
            return new Result.InvalidCode("Código inválido.");
        }

        if (codeEntity.isUsed()) {
            return new Result.AlreadyVerified("El código ya fue utilizado.");
        }

        if (!codeEntity.getCode().equals(inputCode)) {
            return new Result.InvalidCode("El código no coincide con el enviado.");
        }

        codeEntity.setUsed(true);
        codeQuery.save(codeEntity);

        pyme.setActive(true);
        pymeRepository.save(pyme);

        return new Result.Sucess();
    }
}
