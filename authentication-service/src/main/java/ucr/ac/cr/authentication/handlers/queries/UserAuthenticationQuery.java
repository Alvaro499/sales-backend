package ucr.ac.cr.authentication.handlers.queries;


import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;
import ucr.ac.cr.authentication.models.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ucr.ac.cr.authentication.exceptions.BusinessException.exceptionBuilder;

@Service
public class UserAuthenticationQuery {

    @Autowired
    private UserRepository repository;

    public AuthenticatedUser loadUserByUsername(String username) {
        Optional<UserEntity> user = repository.findByEmail(username);

        if (user.isPresent()) {
            return new AuthenticatedUser(
                    user.get().getId(),
                    user.get().getEmail(),
                    user.get().getPassword(),
                    user.get().getRoles()
            );
        } else {
            throw exceptionBuilder().message("User not found with email: " + username).build();
        }
    }
}

