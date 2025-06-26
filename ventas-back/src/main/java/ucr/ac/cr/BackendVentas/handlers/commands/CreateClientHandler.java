package ucr.ac.cr.BackendVentas.handlers.commands;

import java.util.UUID;

public interface CreateClientHandler {
    UUID handle(Command command);

    record Command(UUID clientId) {}
}