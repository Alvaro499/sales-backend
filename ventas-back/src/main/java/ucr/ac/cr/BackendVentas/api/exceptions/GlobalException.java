package ucr.ac.cr.BackendVentas.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ucr.ac.cr.BackendVentas.models.BaseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalException {

    /**
     * Captura todo lo que no sea un BaseException, por ejemplo:
     * <p>
     *      NullPointerException
     *      IllegalStateException
     *      Cualquier bug inesperado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("details", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /** Captura las excepciones personalizadas BaseException, que son
     * las que se lanzan desde los controladores y manejadores de comandos.
     *
     * @param ex La excepción personalizada que contiene el código de error,
     *           mensaje y parámetros adicionales.
     * @return Un ResponseEntity con el código de error, mensaje y parámetros.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "code", ex.getCode(),
                "message", ex.getMessage(),
                "params", ex.getParams()
        ));
    }

}
