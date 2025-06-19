package ucr.ac.cr.BackendVentas.handlers.commands;

import java.util.UUID;

public interface RegisterPymeHandler {

    Result handle(Command command);

    sealed interface Result permits Result.Success, Result.InvalidFields, Result.EmailAlreadyExist, Result.NameAlreadyExist {
        record Success(UUID pymeId) implements Result {}
        record InvalidFields(String... fields) implements Result {}
        record EmailAlreadyExist() implements Result {}
        record NameAlreadyExist() implements Result {}
    }

    record Command(UUID userId, String pymeName, String email, String phone, String address, String description) {}

}
