package ucr.ac.cr.authentication.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucr.ac.cr.authentication.exceptions.BusinessException;
import ucr.ac.cr.authentication.handlers.LoginUserHandler;
import ucr.ac.cr.authentication.models.BaseException;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:5173") // o el dominio de tu frontend
@RestController
@RequestMapping("/api/public/auth")
public class LoginController {

    @Autowired
    private LoginUserHandler loginUserHandler;

    @PostMapping("/loginUser")
    public ResponseEntity<?> login(@RequestBody LoginUserHandler.Command command) {
        try {
            Map<String, Object> loginResponse = loginUserHandler.login(command);

            return ResponseEntity.ok().body(loginResponse);
        } catch (BusinessException ex) {
            return ResponseEntity.badRequest().body(BaseException.exceptionBuilder()
                    .code("INVALID_CREDENTIALS")
                    .message(ex.getMessage())
                    .build());
        }
    }

}
