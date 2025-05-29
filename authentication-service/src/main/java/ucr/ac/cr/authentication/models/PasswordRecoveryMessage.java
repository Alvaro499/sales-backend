package ucr.ac.cr.authentication.models;

public record PasswordRecoveryMessage(String email, String token) {}