package ucr.ac.cr.BackendVentas.handlers.commands;

import java.util.UUID;

public interface RegisterPymeHandler {

    Result handle(Command command);

    sealed interface Result permits Result.Success, Result.InvalidFields, Result.AlreadyExists {
        record Success(UUID pymeId) implements Result {}
        record InvalidFields(String... fields) implements Result {}
        record AlreadyExists() implements Result {}
    }

    record Command(String pymeName, String email, String phone, String address, String password) {}

}
