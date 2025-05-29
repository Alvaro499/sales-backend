package ucr.ac.cr.authentication.handlers.commands;

public interface ResetPasswordHandler {

    Result handle(Command command);

    record Command(String token, String newPassword) {}

    sealed interface Result
            permits Result.Success, Result.InvalidToken, Result.PasswordValidationError, Result.UserNotFound, Result.ResetError {

        record Success() implements Result {}

        record InvalidToken(String message) implements Result {}

        record PasswordValidationError(String message) implements Result {}

        record UserNotFound(String message) implements Result {}

        record ResetError(String message) implements Result {}
    }
}
