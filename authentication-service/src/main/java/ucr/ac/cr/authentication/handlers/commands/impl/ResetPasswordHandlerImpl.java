package ucr.ac.cr.authentication.handlers.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ucr.ac.cr.authentication.handlers.commands.ResetPasswordHandler;
import ucr.ac.cr.authentication.handlers.queries.impl.PasswordResetTokenQueryImpl;
import ucr.ac.cr.authentication.jpa.entities.PasswordResetTokenEntity;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.repositories.PasswordResetTokenRepository;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ResetPasswordHandlerImpl implements ResetPasswordHandler {

    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetTokenQueryImpl tokenQuery;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public ResetPasswordHandlerImpl(
            PasswordResetTokenRepository tokenRepository,
            PasswordResetTokenQueryImpl tokenQuery,
            UserRepository userRepository,
            PasswordEncoder encoder
    ) {
        this.tokenRepository = tokenRepository;
        this.tokenQuery = tokenQuery;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public Result handle(Command command) {
        try {
            Optional<PasswordResetTokenEntity> tokenOpt = tokenQuery.findByToken(command.token());
            if (tokenOpt.isEmpty()) {
                return new Result.InvalidToken("Invalid token.");
            }

            PasswordResetTokenEntity token = tokenOpt.get();

            Result tokenValidation = validateToken(token);
            if (!(tokenValidation instanceof Result.Success)) return tokenValidation;

            Result passwordValidation = validateNewPassword(command.newPassword());
            if (!(passwordValidation instanceof Result.Success)) return passwordValidation;

            return updatePassword(token, command.newPassword());

        } catch (DataAccessException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result.DatabaseError("Falló la recuperación de la contraseña");
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result.DatabaseError("Ocurrió un error inesperado.");
        }
    }

    //Versión con try-catch reducido (no borrar)
    /*
    @Override
@Transactional
public Result handle(Command command) {
    Optional<PasswordResetTokenEntity> tokenOpt = tokenQuery.findByToken(command.token());
    if (tokenOpt.isEmpty()) {
        return new Result.InvalidToken("Invalid token.");
    }

    PasswordResetTokenEntity token = tokenOpt.get();

    Result tokenValidation = validateToken(token);
    if (!(tokenValidation instanceof Result.Success)) return tokenValidation;

    Result passwordValidation = validateNewPassword(command.newPassword());
    if (!(passwordValidation instanceof Result.Success)) return passwordValidation;

    try {
        return updatePassword(token, command.newPassword());
    } catch (DataAccessException ex) {
        return new Result.ResetError("Falló la recuperación de la contraseña. Intente más tarde.");
    }
}

    * */


    private Result validateToken(PasswordResetTokenEntity token) {
        if (token.isUsed()) {
            return new Result.InvalidToken("El token ya ha sido utilizado");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new Result.InvalidToken("El token ha expirado.");
        }

        return new Result.Success();
    }

    private Result validateNewPassword(String password) {
        if (password == null || password.isBlank()) {
            return new Result.InvalidPassword("Contraseña no puede estar vacía");
        }

        if (password.length() < 8) {
            return new Result.InvalidPassword("La contraseña debe tener mínimo 8 carácteres");
        }

        return new Result.Success();
    }

    private Result updatePassword(PasswordResetTokenEntity token, String rawPassword) {
        UserEntity user = token.getUser();

        if (user == null) {
            return new Result.UserNotFound("Usuario no encontrado");
        }

        user.setPassword(encoder.encode(rawPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return new Result.Success();
    }
}
