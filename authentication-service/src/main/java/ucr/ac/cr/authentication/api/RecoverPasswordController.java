package ucr.ac.cr.authentication.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.authentication.api.types.RecoverPasswordRequest;
import ucr.ac.cr.authentication.api.types.ResetPasswordRequest;
import ucr.ac.cr.authentication.handlers.commands.RecoverPasswordHandler;
import ucr.ac.cr.authentication.handlers.commands.ResetPasswordHandler;

@RestController
@CrossOrigin
@RequestMapping("/api/public/auth")
public class RecoverPasswordController {

    private final RecoverPasswordHandler recoverHandler;
    private final ResetPasswordHandler resetHandler;

    public RecoverPasswordController(RecoverPasswordHandler recoverHandler, ResetPasswordHandler resetHandler) {
        this.recoverHandler = recoverHandler;
        this.resetHandler = resetHandler;
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordRequest request) {
        var command = new RecoverPasswordHandler.Command(request.email());

        return handleResultRecoverPassword(recoverHandler.handle(command));
    }



    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        var command = new ResetPasswordHandler.Command(request.token(), request.newPassword());
        return handleResultResetPassword(resetHandler.handle(command));
    }


    private ResponseEntity<?> handleResultRecoverPassword(RecoverPasswordHandler.Result result) {
        return switch (result) {
            case RecoverPasswordHandler.Result.Success success ->
                    ResponseEntity.ok("Email enviado correctamente.");

            case RecoverPasswordHandler.Result.InvalidEmail invalid ->
                    ResponseEntity.badRequest().body(invalid);

            case RecoverPasswordHandler.Result.UserNotFound notFound ->
                    ResponseEntity.status(404).body(notFound);

            case RecoverPasswordHandler.Result.AlreadyRequested alreadyRequested ->
                    ResponseEntity.status(404).body(alreadyRequested);

            case RecoverPasswordHandler.Result.EmailServiceError error ->
                    ResponseEntity.status(500).body(error);
        };
    }


    private ResponseEntity<?> handleResultResetPassword(ResetPasswordHandler.Result result) {
        return switch (result) {
            case ResetPasswordHandler.Result.Success success ->
                    ResponseEntity.ok("Contraseña actualizada correctamente.");

            case ResetPasswordHandler.Result.InvalidToken invalidToken ->
                    ResponseEntity.status(400).body(invalidToken.message());

            case ResetPasswordHandler.Result.UserNotFound userNotFound ->
                    ResponseEntity.status(404).body(userNotFound.message());

            case ResetPasswordHandler.Result.InvalidPassword passwordInvalid ->
                    ResponseEntity.status(400).body(passwordInvalid.message());

            case ResetPasswordHandler.Result.DatabaseError resetError ->
                    ResponseEntity.status(500).body(resetError.message());
        };
    }
}
