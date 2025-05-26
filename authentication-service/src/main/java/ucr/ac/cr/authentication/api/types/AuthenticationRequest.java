package ucr.ac.cr.authentication.api.types;

public record AuthenticationRequest(
        String email,
        String password
) {
}