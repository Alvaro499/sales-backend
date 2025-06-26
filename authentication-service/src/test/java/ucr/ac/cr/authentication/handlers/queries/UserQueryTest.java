package ucr.ac.cr.authentication.handlers.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ucr.ac.cr.authentication.handlers.queries.impl.UserQueryImpl;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserQueryImplTest {

    private UserRepository userRepository;
    private UserQueryImpl userQuery;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userQuery = new UserQueryImpl(userRepository);
    }

    @Test
    void findByEmail_returnsUser_whenUserExists() {
        String email = "test@example.com";
        UserEntity user = new UserEntity();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userQuery.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_returnsEmpty_whenUserDoesNotExist() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userQuery.findByEmail(email);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByEmail(email);
    }
}
