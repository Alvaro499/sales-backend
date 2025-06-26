package ucr.ac.cr.BackendVentas.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.BackendVentas.api.types.PymeResponse;
import ucr.ac.cr.BackendVentas.api.types.RegisterPymeRequest;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;
import ucr.ac.cr.BackendVentas.handlers.commands.RegisterPymeHandler;
import ucr.ac.cr.BackendVentas.models.BaseException;
import ucr.ac.cr.BackendVentas.models.ErrorCode;

import java.util.List;

@RestController
@RequestMapping("/api/pymes")
public class RegisterPymeController {

    @Autowired
    private RegisterPymeHandler registerPymeHandler;

    @Autowired
    private PymeRepository pymeRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerPyme(@RequestBody RegisterPymeRequest request) {
        var command = new RegisterPymeHandler.Command(
                request.userId(),
                request.pymeName(),
                request.email(),
                request.phone(),
                request.address(),
                request.description()
        );

        var result = registerPymeHandler.handle(command);

        return switch (result) {
            case RegisterPymeHandler.Result.Success success ->
                    ResponseEntity.ok(new PymeResponse(success.pymeId(), command.email()));

            case RegisterPymeHandler.Result.InvalidFields invalid ->
                    ResponseEntity.badRequest().body(invalid);

            case RegisterPymeHandler.Result.EmailAlreadyExist emailAlreadyExists -> {
                var exception = BaseException.exceptionBuilder()
                        .code(ErrorCode.REQUIRED_FIELDS)
                        .message("EMAIL_ALREADY_EXISTS")
                        .params(List.of(request.email()))
                        .build();
                yield ResponseEntity.status(409).body(exception);
            }

            case RegisterPymeHandler.Result.NameAlreadyExist nameAlreadyExists -> {
                var exception = BaseException.exceptionBuilder()
                        .code(ErrorCode.REQUIRED_FIELDS)
                        .message("NAME_ALREADY_EXISTS")
                        .params(List.of(request.pymeName()))
                        .build();
                yield ResponseEntity.status(409).body(exception);
            }
        };
    }
}