package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.BackendVentas.events.PymeRegisteredEvent;
import ucr.ac.cr.BackendVentas.events.SendUserPymeIdEvent;
import ucr.ac.cr.BackendVentas.handlers.commands.CreatePymeConfirmationCodeHandler;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;
import ucr.ac.cr.BackendVentas.producers.PymeRegisteredProducer;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.handlers.commands.RegisterPymeHandler;
import ucr.ac.cr.BackendVentas.producers.SendUserPymeIdProducer;

@Component
public class RegisterPymeHandlerImpl implements RegisterPymeHandler {
    private final PymeRepository pymeRepository;
    private final PymeRegisteredProducer pymeRegisteredProducer;
    private final SendUserPymeIdProducer sendUserPymeIdProducer;
    private final CreatePymeConfirmationCodeHandler createPymeCodeHandler;

    @Autowired
    public RegisterPymeHandlerImpl(PymeRepository pymeRepository,
                                   PymeRegisteredProducer pymeRegisteredProducer,
                                   SendUserPymeIdProducer sendUserPymeIdProducer,
                                   CreatePymeConfirmationCodeHandler createPymeCodeHandler) {
        this.pymeRepository = pymeRepository;
        this.pymeRegisteredProducer = pymeRegisteredProducer;
        this.sendUserPymeIdProducer = sendUserPymeIdProducer;
        this.createPymeCodeHandler = createPymeCodeHandler;
    }

    @Transactional
    @Override
    public Result handle(Command command) {
        var invalidFields = validateFields(command);
        if (invalidFields != null) return invalidFields;

        if (pymeRepository.existsByEmail(command.email())) {
            return new Result.EmailAlreadyExist();
        }

        if (pymeRepository.existsByName(command.pymeName())) {
            return new Result.NameAlreadyExist();
        }

        var pyme = new PymeEntity();
        pyme.setName(command.pymeName());
        pyme.setUserId(command.userId());
        pyme.setEmail(command.email());
        pyme.setPhone(command.phone());
        pyme.setAddress(command.address());
        pyme.setDescription(command.description());
        pyme.setActive(false);

        PymeEntity savedPyme = pymeRepository.save(pyme);

        String confirmationCode = createPymeCodeHandler.handle(savedPyme);


        sendUserPymeIdProducer.sendUserPymeId(new SendUserPymeIdEvent(savedPyme.getId(), command.userId()));


        PymeRegisteredEvent event = new PymeRegisteredEvent(savedPyme.getEmail(), confirmationCode, savedPyme.getName());



        // Enviar el evento a Kafka
        boolean enviado = pymeRegisteredProducer.sendEmailConfirmation(event);
        if (!enviado) {
            return new Result.InvalidFields("Error al enviar el correo de confirmación.");
        }

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
