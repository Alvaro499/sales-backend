package ucr.ac.cr.authentication.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ucr.ac.cr.authentication.handlers.queries.UserQuery;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;

import java.util.Optional;

@Component
public class UserQueryImpl implements UserQuery {

    private final UserRepository userRepository;

    @Autowired
    public UserQueryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}