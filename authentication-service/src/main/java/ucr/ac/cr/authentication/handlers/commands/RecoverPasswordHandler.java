package ucr.ac.cr.authentication.handlers.commands;


public interface RecoverPasswordHandler {

    Result handle(Command command);

    record Command(String email) {}

    sealed interface Result
            permits Result.Success, Result.InvalidEmail, Result.UserNotFound, Result.AlreadyRequested, Result.EmailServiceError {

        record Success() implements Result {}

        record InvalidEmail(String message) implements Result {}

        record UserNotFound(String message) implements Result {}

        record AlreadyRequested(String message) implements Result {}

        record EmailServiceError(String message) implements Result {}
    }
}