package ucr.ac.cr.authentication.api.types;


public record RegisterRequest(
        String name,
        String email,
        String password
) {
}

