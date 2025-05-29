package ucr.ac.cr.authentication.handlers.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ucr.ac.cr.authentication.handlers.commands.ResetPasswordHandler;
import ucr.ac.cr.authentication.jpa.entities.UserEntity;
import ucr.ac.cr.authentication.jpa.entities.UserRecoveryTokenEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserRecoveryTokenRepository;
import ucr.ac.cr.authentication.jpa.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class ResetPasswordHandlerImpl implements ResetPasswordHandler {


    private final UserRecoveryTokenRepository userRecoveryTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public ResetPasswordHandlerImpl(UserRecoveryTokenRepository userRecoveryTokenRepository,
                                    UserRepository userRepository) {
        this.userRecoveryTokenRepository = userRecoveryTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Result handle(Command command) {
        return null;
    }

    public Result validateRecoveryToken(UUID token) {
        Optional<UserRecoveryTokenEntity> tokenOpt = userRecoveryTokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return new Result.InvalidToken("Token inválido.");
        }

        UserRecoveryTokenEntity recoveryToken = tokenOpt.get();

        if (recoveryToken.isUsed()) {
            return new Result.InvalidToken("El token ya fue usado.");
        }

        if (recoveryToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new Result.InvalidToken("El token ha expirado.");
        }

        return new Result.Success();
    }

    private Result resetPassword(UUID token, String newPassword) {
        Result validationResult = validateRecoveryToken(token);
        if (!(validationResult instanceof Result.Success)) {
            return validationResult;
        }

        UserRecoveryTokenEntity recoveryToken = userRecoveryTokenRepository.findByToken(token).get();
        UserEntity user = recoveryToken.getUser();

        //falta el hash
        user.setPassword(newPassword);
        userRepository.save(user);

        // Marcar token como usado
        markTokenAsUsed(token);

        return new Result.Success();
    }

    public void markTokenAsUsed(UUID token) {
        Optional<UserRecoveryTokenEntity> tokenOpt = userRecoveryTokenRepository.findByToken(token);

        tokenOpt.ifPresent(t -> {
            t.setUsed(true);
            userRecoveryTokenRepository.save(t);
        });
    }
}
