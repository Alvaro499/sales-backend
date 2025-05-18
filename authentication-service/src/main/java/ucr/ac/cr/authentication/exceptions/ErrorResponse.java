package ucr.ac.cr.authentication.exceptions;


import java.util.UUID;

public record ErrorResponse(
        String message,
        int code,
        UUID correlationId
) {

}