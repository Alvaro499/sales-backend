package ucr.ac.cr.BackendVentas.api.types;

import java.util.UUID;

public record RegisterPymeRequest(
    UUID userId,
    String pymeName,
    String email,
    String phone,
    String address,
    String description
) {}