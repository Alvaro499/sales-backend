package ucr.ac.cr.email_service.models;

public record PasswordRecoveryMessage(String email, String token) {}
