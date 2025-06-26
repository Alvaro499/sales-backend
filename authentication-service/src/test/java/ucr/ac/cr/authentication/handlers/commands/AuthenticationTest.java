package ucr.ac.cr.authentication.handlers.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ucr.ac.cr.authentication.exceptions.BusinessException;
import ucr.ac.cr.authentication.handlers.queries.UserAuthenticationQuery;
import ucr.ac.cr.authentication.http.JwtService;
import ucr.ac.cr.authentication.models.AuthenticatedUser;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationTest {

    @Mock
    private UserAuthenticationQuery userAuthenticationQuery;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationHandler authenticationHandler;

    private final UUID userId = UUID.randomUUID();
    private final String username = "testuser";
    private final String rawPassword = "password123";
    private final String encodedPassword = "encoded123";
    private final List<String> roles = List.of("ROLE_USER");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        // Arrange
        AuthenticatedUser user = new AuthenticatedUser(userId, username, encodedPassword, roles);
        when(userAuthenticationQuery.loadUserByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        // Act
        String token = authenticationHandler.authenticateWithJwt(username, rawPassword);

        // Assert
        assertEquals("fake-jwt-token", token);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        // Arrange
        when(userAuthenticationQuery.loadUserByUsername(username)).thenReturn(null);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () ->
                authenticationHandler.authenticateWithJwt(username, rawPassword));
        assertEquals("User not provided", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Arrange
        AuthenticatedUser user = new AuthenticatedUser(userId, username, null, roles);
        when(userAuthenticationQuery.loadUserByUsername(username)).thenReturn(user);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () ->
                authenticationHandler.authenticateWithJwt(username, rawPassword));
        assertEquals("Password not provided", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Arrange
        AuthenticatedUser user = new AuthenticatedUser(userId, username, encodedPassword, roles);
        when(userAuthenticationQuery.loadUserByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () ->
                authenticationHandler.authenticateWithJwt(username, rawPassword));
        assertEquals("Invalid user", ex.getMessage());
    }
}
