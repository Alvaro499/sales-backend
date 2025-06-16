package ucr.ac.cr.email_service.events;

public record PasswordRecoveryMessage(String email, String token) {}
