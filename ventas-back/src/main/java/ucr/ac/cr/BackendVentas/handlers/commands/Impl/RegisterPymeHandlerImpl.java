package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;
import ucr.ac.cr.BackendVentas.service.EmailService;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.handlers.commands.RegisterPymeHandler;



@Component
public class RegisterPymeHandlerImpl implements RegisterPymeHandler {
    private final PymeRepository pymeRepository;
    private final EmailService emailService;

    @Autowired
    public RegisterPymeHandlerImpl(PymeRepository pymeRepository, EmailService emailService) {
        this.pymeRepository = pymeRepository;
        this.emailService = emailService;
    }

    @Override
    public Result handle(Command command) {
        var invalidFields = validateFields(command);
        if (invalidFields != null) {
            return invalidFields;
        }

        boolean emailExist = pymeRepository.existsByEmail(command.email());
        if (emailExist) {
            return new Result.EmailAlreadyExist();
        }
        boolean nameExist = pymeRepository.existsByName(command.pymeName());
        if (nameExist) {
            return new Result.NameAlreadyExist();
        }


        PymeEntity pyme = new PymeEntity();
        pyme.setName(command.pymeName());
        pyme.setEmail(command.email());
        pyme.setPhone(command.phone());
        pyme.setAddress(command.address());
        pyme.setDescription(command.description());

        PymeEntity savedPyme = pymeRepository.save(pyme);

        emailService.sendEmailEvent(savedPyme.getEmail());
        return new Result.Success(savedPyme.getId());
    }

    private Result validateFields(Command command) {
        if (command.pymeName() == null || command.pymeName().isEmpty()) {
            return new Result.InvalidFields("pymeName");
        }
        if (command.email() == null || command.email().isEmpty()) {
            return new Result.InvalidFields("email");
        }
        if (command.phone() == null || command.phone().isEmpty()) {
            return new Result.InvalidFields("phone");
        }
        if (command.address() == null || command.address().isEmpty()) {
            return new Result.InvalidFields("address");
        }

        return null;
    }
}
