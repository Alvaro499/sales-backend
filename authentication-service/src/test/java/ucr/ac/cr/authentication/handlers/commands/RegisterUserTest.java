package ucr.ac.cr.authentication.handlers.commands;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ucr.ac.cr.authentication.exceptions.InvalidInputException;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;

public class RegisterUserTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private RegisterUserHandler handler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        RegisterUserHandler.Command command = new RegisterUserHandler.Command(null, "Name", "password123");

        assertThatThrownBy(() -> handler.register(command))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Invalid Field email");

        verifyNoInteractions(repository);
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        RegisterUserHandler.Command command = new RegisterUserHandler.Command("user@example.com", null, "password123");

        assertThatThrownBy(() -> handler.register(command))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Invalid Field name");

        verifyNoInteractions(repository);
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        RegisterUserHandler.Command command = new RegisterUserHandler.Command("user@example.com", "Name", null);

        assertThatThrownBy(() -> handler.register(command))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Invalid Field password");

        verifyNoInteractions(repository);
        verifyNoInteractions(encoder);
    }
}
