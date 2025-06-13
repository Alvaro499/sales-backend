package ucr.ac.cr.authentication.api.types;

public record ConfirmationCodeRequest(String email, String code) {}