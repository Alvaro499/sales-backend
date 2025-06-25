package ucr.ac.cr.BackendVentas.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ucr.ac.cr.BackendVentas.handlers.queries.impl.PymeConfirmationCodeQueryImpl;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeConfirmationCodeEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeConfirmationCodeRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PymeConfirmationCodeQuerylTest {

    private PymeConfirmationCodeRepository repository;
    private PymeConfirmationCodeQueryImpl queryImpl;

    @BeforeEach
    void setUp() {
        repository = mock(PymeConfirmationCodeRepository.class);
        queryImpl = new PymeConfirmationCodeQueryImpl(repository);
    }

    @Test
    void shouldFindValidByPymeId() {
        UUID pymeId = UUID.randomUUID();
        PymeConfirmationCodeEntity entity = new PymeConfirmationCodeEntity();
        when(repository.findFirstByPymeIdAndUsedFalse(pymeId)).thenReturn(Optional.of(entity));

        Optional<PymeConfirmationCodeEntity> result = queryImpl.findValidByPymeId(pymeId);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(repository).findFirstByPymeIdAndUsedFalse(pymeId);
    }

    @Test
    void shouldReturnEmptyWhenNoValidCodeFound() {
        UUID pymeId = UUID.randomUUID();
        when(repository.findFirstByPymeIdAndUsedFalse(pymeId)).thenReturn(Optional.empty());

        Optional<PymeConfirmationCodeEntity> result = queryImpl.findValidByPymeId(pymeId);

        assertTrue(result.isEmpty());
        verify(repository).findFirstByPymeIdAndUsedFalse(pymeId);
    }

    @Test
    void shouldSaveEntity() {
        PymeConfirmationCodeEntity entity = new PymeConfirmationCodeEntity();
        when(repository.save(entity)).thenReturn(entity);

        PymeConfirmationCodeEntity result = queryImpl.save(entity);

        assertEquals(entity, result);
        verify(repository).save(entity);
    }

    @Test
    void shouldFindLatestByPymeId() {
        UUID pymeId = UUID.randomUUID();
        PymeConfirmationCodeEntity entity = new PymeConfirmationCodeEntity();
        when(repository.findTopByPymeIdOrderByCreatedAtDesc(pymeId)).thenReturn(Optional.of(entity));

        Optional<PymeConfirmationCodeEntity> result = queryImpl.findLatestByPymeId(pymeId);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(repository).findTopByPymeIdOrderByCreatedAtDesc(pymeId);
    }

    @Test
    void shouldReturnEmptyWhenNoLatestCodeFound() {
        UUID pymeId = UUID.randomUUID();
        when(repository.findTopByPymeIdOrderByCreatedAtDesc(pymeId)).thenReturn(Optional.empty());

        Optional<PymeConfirmationCodeEntity> result = queryImpl.findLatestByPymeId(pymeId);

        assertTrue(result.isEmpty());
        verify(repository).findTopByPymeIdOrderByCreatedAtDesc(pymeId);
    }
}
