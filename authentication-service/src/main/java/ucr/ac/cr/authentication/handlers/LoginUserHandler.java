package ucr.ac.cr.authentication.handlers;

import ucr.ac.cr.authentication.exceptions.BusinessException;
import ucr.ac.cr.authentication.http.JwtService;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.entities.UserPymeEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserPymeRepository;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ucr.ac.cr.authentication.models.AuthenticatedUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class LoginUserHandler {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserPymeRepository userPymeRepository;

    public record Command(String email, String password) {}

    public Map<String, Object> login(Command command) {
        validateRequiredFields(command);
        UserEntity user = findUserByEmail(command.email());
        if (user == null) {
            throw new BusinessException("Invalid email or password");
        }

        if (!encoder.matches(command.password(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        // Buscar el pymeId asociado al userId
        UUID pymeId = findPymeIdByUserId(user.getId());

        // Generar el token
        String token = generateToken(user);

        // Devolver un mapa con el token y el pymeId
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("pymeId", pymeId);
        return response;
    }

    private void validateRequiredFields(Command command) {
        if (command.email() == null) {
            throw new BusinessException("email is required");
        }
        if (command.password() == null) {
            throw new BusinessException("password is required");
        }
    }

    private UUID findPymeIdByUserId(UUID userId) {
        Optional<UserPymeEntity> userPymeOptional = userPymeRepository.findByUserId(userId);
        return userPymeOptional.map(UserPymeEntity::getPymeId).orElse(null);
    }

    private UserEntity findUserByEmail(String email) {
        Optional<UserEntity> userOptional = repository.findByEmail(email);
        return userOptional.orElse(null);
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
