package ucr.ac.cr.authentication.handlers.commands;

import ucr.ac.cr.authentication.exceptions.BusinessException;
import ucr.ac.cr.authentication.http.JwtService;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ucr.ac.cr.authentication.models.AuthenticatedUser;

import java.util.Optional;

@Component
public class LoginUserHandler {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    // Record for the login command
    public record Command(String email, String password) {}

    public String login(Command command) {
        validateRequiredFields(command);
        UserEntity user = findUserByEmail(command.email());
        if (user == null) {
            throw new BusinessException("Invalid email or password");
        }

        // Check if the password matches
        if (!encoder.matches(command.password(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        // Generate a JWT or any other token here, assuming a method exists
        return generateToken(user);
    }

    private void validateRequiredFields(Command command) {
        if (command.email() == null) {
            throw new BusinessException("email is required");
        }
        if (command.password() == null) {
            throw new BusinessException("password is required");
        }
    }

    private UserEntity findUserByEmail(String email) {
        Optional<UserEntity> userOptional = repository.findByEmail(email);
        return userOptional.orElse(null);  // Return null if not found
    }

    private String generateToken(UserEntity user) {

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles()
        );
        return jwtService.generateToken(authenticatedUser);
    }
}
