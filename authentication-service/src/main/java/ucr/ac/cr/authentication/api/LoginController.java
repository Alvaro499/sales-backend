package ucr.ac.cr.authentication.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.authentication.exceptions.BusinessException;
import ucr.ac.cr.authentication.handlers.commands.LoginUserHandler;
import ucr.ac.cr.authentication.models.BaseException;

@RestController
@RequestMapping("/api/public/auth")
public class LoginController {

    @Autowired
    private LoginUserHandler loginUserHandler;

    @PostMapping("/loginUser")
    public ResponseEntity<?> login(@RequestBody LoginUserHandler.Command command) {
        try {
            String token = loginUserHandler.login(command);

            return ResponseEntity.ok().body(token);
        } catch (BusinessException ex) {
            return ResponseEntity.badRequest().body(BaseException.exceptionBuilder()
                    .code("INVALID_CREDENTIALS")
                    .message(ex.getMessage())
                    .build());
        }
    }
}
