package ucr.ac.cr.BackendVentas.handlers.commands;

import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

public interface CreatePymeConfirmationCodeHandler {
    String handle(PymeEntity pyme);
}