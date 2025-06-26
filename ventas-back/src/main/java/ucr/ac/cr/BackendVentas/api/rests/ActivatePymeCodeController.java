package ucr.ac.cr.BackendVentas.api.rests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.BackendVentas.api.types.ActivatePymeCodeRequest;
import ucr.ac.cr.BackendVentas.handlers.commands.ActivatePymeCodeHandler;
import ucr.ac.cr.BackendVentas.jpa.repositories.PymeRepository;
import ucr.ac.cr.BackendVentas.models.BaseException;
import ucr.ac.cr.BackendVentas.models.ErrorCode;


import java.util.List;

@RestController
@RequestMapping("/api/pymes")
public class ActivatePymeCodeController {

    @Autowired
    private PymeRepository pymeRepository;

    @Autowired
    private ActivatePymeCodeHandler activatePymeCodeHandler;

    @PostMapping("/activate")
    public ResponseEntity<?> activateCode(@RequestBody ActivatePymeCodeRequest request) {
        var command = new ActivatePymeCodeHandler.Command(
                request.userId(),
                request.code()
        );

        var result = activatePymeCodeHandler.handle(command);

        return switch (result) {
            case ActivatePymeCodeHandler.Result.Sucess success ->
                    ResponseEntity.ok("Código verificado correctamente.");

            case ActivatePymeCodeHandler.Result.InvalidCode invalid -> {
                var exception = BaseException.exceptionBuilder()
                        .code(ErrorCode.INVALID_CONFIRMATION_CODE)
                        .message("INVALID_CONFIRMATION_CODE")
                        .params(List.of(invalid.reason()))
                        .build();
                yield ResponseEntity.badRequest().body(exception);
            }

            case ActivatePymeCodeHandler.Result.AlreadyVerified verified -> {
                var exception = BaseException.exceptionBuilder()
                        .code(ErrorCode.ALREADY_VERIFIED)
                        .message("CONFIRMATION_CODE_ALREADY_USED")
                        .params(List.of(verified.reason()))
                        .build();
                yield ResponseEntity.status(409).body(exception);
            }
        };
    }

    @GetMapping
    public ResponseEntity<?> listAllPymes() {
        var pymes = pymeRepository.findAll();
        return ResponseEntity.ok(pymes);
    }
}
