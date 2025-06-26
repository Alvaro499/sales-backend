package ucr.ac.cr.BackendVentas.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ucr.ac.cr.BackendVentas.handlers.commands.CreatePymeConfirmationCodeHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.Impl.CreatePymeConfirmationCodeHandlerImpl;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeConfirmationCodeRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreatePymeConfirmationCodeTest {

    private PymeConfirmationCodeRepository repository;
    private CreatePymeConfirmationCodeHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(PymeConfirmationCodeRepository.class);
        handler = new CreatePymeConfirmationCodeHandlerImpl(repository);
    }

    @Test
    void testGeneraCodigoYGuardaEnRepositorio() {
        // Arrange
        PymeEntity pyme = new PymeEntity();
        pyme.setId(UUID.randomUUID());

        ArgumentCaptor<PymeConfirmationCodeEntity> captor = ArgumentCaptor.forClass(PymeConfirmationCodeEntity.class);

        // Act
        String codigo = handler.handle(pyme);

        // Assert
        assertNotNull(codigo);
        assertEquals(4, codigo.length());
        assertTrue(codigo.matches("\\d{4}")); // Debe ser 4 dígitos

        verify(repository, times(1)).save(captor.capture());

        PymeConfirmationCodeEntity guardado = captor.getValue();
        assertEquals(pyme, guardado.getPyme());
        assertEquals(codigo, guardado.getCode());
        assertFalse(guardado.isUsed());
        assertNotNull(guardado.getCreatedAt());
    }
}
