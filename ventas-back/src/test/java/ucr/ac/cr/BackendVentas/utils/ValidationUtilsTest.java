package ucr.ac.cr.BackendVentas.utils;

import org.junit.jupiter.api.Test;
import ucr.ac.cr.BackendVentas.models.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validateEmail(null)
        );

        assertEquals("REQUIRED_FIELDS", exception.getCode());
        assertEquals("Debe ingresar un email", exception.getMessage());
        assertTrue(exception.getParams().contains("email"));
    }

    @Test
    void shouldThrowExceptionForInvalidEmailFormat() {
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validateEmail("invalido.com")
        );

        assertEquals("INVALID_FORMAT", exception.getCode());
        assertEquals("El formato del email es inválido", exception.getMessage());
    }

    @Test
    void shouldPassForValidEmail() {
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("test@email.com"));
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validateName("firstName", "")
        );
        assertEquals("REQUIRED_FIELDS", exception.getCode());
        assertEquals("Debes ingresar firstName", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameExceedsMaxLength() {
        String longName = "a".repeat(101);
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validateName("lastName", longName)
        );
        assertEquals("INVALID_FORMAT", exception.getCode());
        assertTrue(exception.getMessage().contains("no puede tener más de 100 caracteres"));
    }

    @Test
    void shouldPassForValidName() {
        assertDoesNotThrow(() -> ValidationUtils.validateName("lastName", "Ramírez"));
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validatePhone(null)
        );
        assertEquals("REQUIRED_FIELDS", exception.getCode());
    }

    @Test
    void shouldThrowExceptionForInvalidPhoneFormat() {
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validatePhone("abc123")
        );
        assertEquals("INVALID_FORMAT", exception.getCode());
    }

    @Test
    void shouldPassForValidPhone() {
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("8888-8888"));
        assertDoesNotThrow(() -> ValidationUtils.validatePhone("88888888"));
    }

    @Test
    void shouldThrowExceptionWhenAddressIsNull() {
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validateShippingAddress(null)
        );
        assertEquals("REQUIRED_FIELDS", exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenAddressTooLong() {
        String longAddress = "a".repeat(201);
        BaseException exception = assertThrows(BaseException.class, () ->
                ValidationUtils.validateShippingAddress(longAddress)
        );
        assertEquals("INVALID_FORMAT", exception.getCode());
    }

    @Test
    void shouldPassForValidShippingAddress() {
        assertDoesNotThrow(() ->
                ValidationUtils.validateShippingAddress("San José, Av. Central, Edificio XYZ")
        );
    }
}
