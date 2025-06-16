package ucr.ac.cr.BackendVentas.events;

public record PymeRegisteredEvent(
        String email,
        String code,
        String pymeName
) {}