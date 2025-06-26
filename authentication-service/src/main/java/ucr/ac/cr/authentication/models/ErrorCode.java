package ucr.ac.cr.authentication.models;

public enum ErrorCode {

    GENERAL_ERROR("GENERAL_ERROR", "Un error general ocurrió"),
    USER_NOT_FOUND("USER_NOT_FOUND", "El usuario no fue encontrado"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Credenciales inválidas"),
    REQUIRED_FIELDS("REQUIRED_FIELDS", "Campos requeridos faltantes");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
