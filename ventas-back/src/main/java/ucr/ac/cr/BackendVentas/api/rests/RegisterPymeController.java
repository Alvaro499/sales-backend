package ucr.ac.cr.BackendVentas.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ucr.ac.cr.BackendVentas.api.types.RegisterPymeRequest;
import ucr.ac.cr.BackendVentas.handlers.commands.RegisterPymeHandler;

@RestController
@RequestMapping("/api/pymes")
public class RegisterPymeController {

    @Autowired
    private RegisterPymeHandler registerPymeHandler;

    @PostMapping("/register")
    public ResponseEntity<?> registerPyme(@RequestBody RegisterPymeRequest request) {
        var command = new RegisterPymeHandler.Command(
                request.pymeName(),
                request.email(),
                request.phone(),
                request.address(),
                request.password(),
                request.description()
        );

        var result = registerPymeHandler.handle(command);

        return switch (result) {
            case RegisterPymeHandler.Result.Success success -> ResponseEntity.ok(success);
            case RegisterPymeHandler.Result.InvalidFields invalid ->
                    ResponseEntity.badRequest().body(invalid);
            case RegisterPymeHandler.Result.AlreadyExists alreadyExists ->
                    ResponseEntity.status(409).body(alreadyExists);
        };
    }
}
