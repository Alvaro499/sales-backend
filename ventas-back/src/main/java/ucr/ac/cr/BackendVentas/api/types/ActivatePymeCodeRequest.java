package ucr.ac.cr.BackendVentas.api.types;

import java.util.UUID;

public record ActivatePymeCodeRequest(
        UUID userId,
        String code
) {}
