package ucr.ac.cr.authentication.handlers.commands;

public interface ResetPasswordHandler {

    Result handle(Command command);

    record Command(String token, String newPassword) {}

    sealed interface Result
            permits Result.Success, Result.InvalidToken, Result.InvalidPassword, Result.UserNotFound, Result.DatabaseError {

        record Success() implements Result {}

        record InvalidToken(String message) implements Result {}

        record InvalidPassword(String message) implements Result {}

        record UserNotFound(String message) implements Result {}

        record DatabaseError(String message) implements Result {}
    }
}
