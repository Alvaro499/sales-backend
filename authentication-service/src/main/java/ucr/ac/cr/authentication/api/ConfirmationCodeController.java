package ucr.ac.cr.authentication.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.authentication.handlers.commands.ConfirmationCodeHandler;
import ucr.ac.cr.authentication.api.types.ConfirmationCodeRequest;

@RestController
@CrossOrigin
@RequestMapping("/api/public/pymes")
public class ConfirmationCodeController {

    private final ConfirmationCodeHandler handler;

    public ConfirmationCodeController(ConfirmationCodeHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/send-confirmation-code")
    public ResponseEntity<?> sendCode(@RequestBody ConfirmationCodeRequest request) {
        var command = new ConfirmationCodeHandler.Command(request.email(), request.code());
        ConfirmationCodeHandler.Result result = handler.handle(command);
        return handleResult(result);
    }

    private ResponseEntity<?> handleResult(ConfirmationCodeHandler.Result result) {
        return switch (result) {
            case ConfirmationCodeHandler.Result.Success success ->
                    ResponseEntity.ok("El código fue enviado correctamente.");
            case ConfirmationCodeHandler.Result.Verified verified ->
                    ResponseEntity.ok("Código verificado. Pyme activada.");
            case ConfirmationCodeHandler.Result.InvalidEmail invalid ->
                    ResponseEntity.badRequest().body(invalid.message());
            case ConfirmationCodeHandler.Result.PymeNotFound notFound ->
                    ResponseEntity.status(404).body(notFound.message());
            case ConfirmationCodeHandler.Result.AlreadyRequested already ->
                    ResponseEntity.status(429).body(already.message());
            case ConfirmationCodeHandler.Result.InvalidCode invalidCode ->
                    ResponseEntity.status(400).body(invalidCode.message());
            case ConfirmationCodeHandler.Result.CodeExpired expired ->
                    ResponseEntity.status(410).body(expired.message());
            case ConfirmationCodeHandler.Result.EmailServiceError error ->
                    ResponseEntity.status(500).body(error.message());
        };
    }
}
