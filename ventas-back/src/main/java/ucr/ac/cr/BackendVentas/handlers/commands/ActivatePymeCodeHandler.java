package ucr.ac.cr.BackendVentas.handlers.commands;

import java.util.UUID;

public interface ActivatePymeCodeHandler {

    Result handle(Command command);

    record Command(UUID userID, String code) {}

    sealed interface Result {
        record Sucess() implements Result {}
        record InvalidCode(String reason) implements Result {}
        record AlreadyVerified(String reason) implements Result {}
    }
}