package ucr.ac.cr.authentication.handlers.commands;

public interface ConfirmationCodeHandler {
    Result handle(Command command);

    record Command(String email, String code) {}

    sealed interface Result permits
            Result.Success, Result.InvalidEmail, Result.PymeNotFound,
            Result.AlreadyRequested, Result.EmailServiceError,
            Result.Verified, Result.InvalidCode, Result.CodeExpired {

        record Success() implements Result {}
        record Verified() implements Result {}
        record InvalidEmail(String message) implements Result {}
        record PymeNotFound(String message) implements Result {}
        record AlreadyRequested(String message) implements Result {}
        record EmailServiceError(String message) implements Result {}
        record InvalidCode(String message) implements Result {}
        record CodeExpired(String message) implements Result {}

    }
}