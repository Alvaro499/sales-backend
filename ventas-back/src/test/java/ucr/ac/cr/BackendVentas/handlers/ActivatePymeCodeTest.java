package ucr.ac.cr.BackendVentas.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucr.ac.cr.BackendVentas.handlers.commands.ActivatePymeCodeHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.Impl.ActivatePymeCodeHandlerImpl;
import ucr.ac.cr.BackendVentas.handlers.queries.PymeConfirmationCodeQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.impl.PymeQueryImpl;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeConfirmationCodeRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActivatePymeCodeTest {

    private PymeQueryImpl pymeQueryImpl;
    private PymeRepository pymeRepository;
    private PymeConfirmationCodeRepository codeRepository;
    private PymeConfirmationCodeQuery codeQuery;

    private ActivatePymeCodeHandler handler;

    private UUID userId;
    private UUID pymeId;
    private PymeEntity pyme;
    private PymeConfirmationCodeEntity code;

    @BeforeEach
    void setUp() {
        pymeQueryImpl = mock(PymeQueryImpl.class);
        pymeRepository = mock(PymeRepository.class);
        codeRepository = mock(PymeConfirmationCodeRepository.class);
        codeQuery = mock(PymeConfirmationCodeQuery.class);

        handler = new ActivatePymeCodeHandlerImpl(
                pymeQueryImpl,
                pymeRepository,
                codeRepository,
                codeQuery
        );

        userId = UUID.randomUUID();
        pymeId = UUID.randomUUID();

        pyme = new PymeEntity();
        pyme.setId(pymeId);
        pyme.setActive(false);

        code = new PymeConfirmationCodeEntity();
        code.setCode("1234");
        code.setUsed(false);
        code.setPyme(pyme);
    }

    @Test
    void shouldActivatePymeWhenCodeIsValid() {
        // Arrange
        when(pymeQueryImpl.findByUserId(userId)).thenReturn(Optional.of(pyme));
        when(codeQuery.findLatestByPymeId(pymeId)).thenReturn(Optional.of(code));
        ActivatePymeCodeHandler.Command command = new ActivatePymeCodeHandler.Command(userId, "1234");

        // Act
        ActivatePymeCodeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(ActivatePymeCodeHandler.Result.Sucess.class, result);
        assertTrue(pyme.getActive());
        assertTrue(code.isUsed());
        verify(pymeRepository).save(pyme);
        verify(codeQuery).save(code);
    }

    @Test
    void shouldReturnInvalidCodeWhenCodeIsIncorrect() {
        // Arrange
        when(pymeQueryImpl.findByUserId(userId)).thenReturn(Optional.of(pyme));
        when(codeQuery.findLatestByPymeId(pymeId)).thenReturn(Optional.of(code));
        ActivatePymeCodeHandler.Command command = new ActivatePymeCodeHandler.Command(userId, "9999");

        // Act
        ActivatePymeCodeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(ActivatePymeCodeHandler.Result.InvalidCode.class, result);
    }

    @Test
    void shouldReturnAlreadyVerifiedWhenCodeIsUsed() {
        // Arrange
        code.setUsed(true);
        when(pymeQueryImpl.findByUserId(userId)).thenReturn(Optional.of(pyme));
        when(codeQuery.findLatestByPymeId(pymeId)).thenReturn(Optional.of(code));
        ActivatePymeCodeHandler.Command command = new ActivatePymeCodeHandler.Command(userId, "1234");

        // Act
        ActivatePymeCodeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(ActivatePymeCodeHandler.Result.AlreadyVerified.class, result);
    }

    @Test
    void shouldReturnInvalidCodeWhenCodeIsEmptyOrBlank() {
        // Arrange
        when(pymeQueryImpl.findByUserId(userId)).thenReturn(Optional.of(pyme));
        when(codeQuery.findLatestByPymeId(pymeId)).thenReturn(Optional.of(code));
        ActivatePymeCodeHandler.Command command = new ActivatePymeCodeHandler.Command(userId, "  ");

        // Act
        ActivatePymeCodeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(ActivatePymeCodeHandler.Result.InvalidCode.class, result);
    }

    @Test
    void shouldReturnInvalidCodeWhenPymeDoesNotExist() {
        // Arrange
        when(pymeQueryImpl.findByUserId(userId)).thenReturn(Optional.empty());
        ActivatePymeCodeHandler.Command command = new ActivatePymeCodeHandler.Command(userId, "1234");

        // Act
        ActivatePymeCodeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(ActivatePymeCodeHandler.Result.InvalidCode.class, result);
    }

    @Test
    void shouldReturnInvalidCodeWhenCodeNotFound() {
        // Arrange
        when(pymeQueryImpl.findByUserId(userId)).thenReturn(Optional.of(pyme));
        when(codeQuery.findLatestByPymeId(pymeId)).thenReturn(Optional.empty());
        ActivatePymeCodeHandler.Command command = new ActivatePymeCodeHandler.Command(userId, "1234");

        // Act
        ActivatePymeCodeHandler.Result result = handler.handle(command);

        // Assert
        assertInstanceOf(ActivatePymeCodeHandler.Result.InvalidCode.class, result);
    }
}
