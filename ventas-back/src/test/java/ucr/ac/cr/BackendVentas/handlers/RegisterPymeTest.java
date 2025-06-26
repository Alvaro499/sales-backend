package ucr.ac.cr.BackendVentas.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ucr.ac.cr.BackendVentas.handlers.commands.RegisterPymeHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;
import ucr.ac.cr.BackendVentas.producers.PymeRegisteredProducer;
import ucr.ac.cr.BackendVentas.producers.SendUserPymeIdProducer;
import ucr.ac.cr.BackendVentas.handlers.commands.CreatePymeConfirmationCodeHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.Impl.RegisterPymeHandlerImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterPymeTest {

    private PymeRepository pymeRepository;
    private PymeRegisteredProducer pymeRegisteredProducer;
    private SendUserPymeIdProducer sendUserPymeIdProducer;
    private CreatePymeConfirmationCodeHandler confirmationCodeHandler;

    private RegisterPymeHandler handler;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        pymeRepository = mock(PymeRepository.class);
        pymeRegisteredProducer = mock(PymeRegisteredProducer.class);
        sendUserPymeIdProducer = mock(SendUserPymeIdProducer.class);
        confirmationCodeHandler = mock(CreatePymeConfirmationCodeHandler.class);

        handler = new RegisterPymeHandlerImpl(
                pymeRepository,
                pymeRegisteredProducer,
                sendUserPymeIdProducer,
                confirmationCodeHandler
        );
    }

    @Test
    void testRegistroExitoso() {
        // Arrange
        RegisterPymeHandler.Command command = new RegisterPymeHandler.Command(
                userId, "Tienda CR", "contacto@cr.com", "8888-8888", "San José", "Tienda"
        );

        when(pymeRepository.existsByEmail(command.email())).thenReturn(false);
        when(pymeRepository.existsByName(command.pymeName())).thenReturn(false);

        PymeEntity entity = new PymeEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail(command.email());
        entity.setName(command.pymeName());

        when(pymeRepository.save(any())).thenReturn(entity);
        when(confirmationCodeHandler.handle(entity)).thenReturn("ABC123");
        when(pymeRegisteredProducer.sendEmailConfirmation(any())).thenReturn(true);

        // Act
        RegisterPymeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(RegisterPymeHandler.Result.Success.class, result);
    }

    @Test
    void testCampoRequeridoFaltante() {
        // Arrange
        RegisterPymeHandler.Command command = new RegisterPymeHandler.Command(
                userId, "", "contacto@cr.com", "8888-8888", "San José", "Tienda"
        );

        // Act
        RegisterPymeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(RegisterPymeHandler.Result.InvalidFields.class, result);
        RegisterPymeHandler.Result.InvalidFields invalid = (RegisterPymeHandler.Result.InvalidFields) result;
        assertTrue(invalid.fields()[0].contains("pymeName"));
    }

    @Test
    void testEmailYaRegistrado() {
        // Arrange
        RegisterPymeHandler.Command command = new RegisterPymeHandler.Command(
                userId, "Tienda CR", "duplicado@cr.com", "8888-8888", "San José", "Tienda"
        );

        when(pymeRepository.existsByEmail(command.email())).thenReturn(true);

        // Act
        RegisterPymeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(RegisterPymeHandler.Result.EmailAlreadyExist.class, result);
    }

    @Test
    void testNombreYaRegistrado() {
        // Arrange
        RegisterPymeHandler.Command command = new RegisterPymeHandler.Command(
                userId, "Tienda CR", "nuevo@cr.com", "8888-8888", "San José", "Tienda"
        );

        when(pymeRepository.existsByEmail(command.email())).thenReturn(false);
        when(pymeRepository.existsByName(command.pymeName())).thenReturn(true);

        // Act
        RegisterPymeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(RegisterPymeHandler.Result.NameAlreadyExist.class, result);
    }
}
