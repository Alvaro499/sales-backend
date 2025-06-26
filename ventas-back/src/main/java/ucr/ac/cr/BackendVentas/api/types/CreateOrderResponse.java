package ucr.ac.cr.BackendVentas.api.types;

import java.util.List;
import java.util.UUID;

public record CreateOrderResponse(
        UUID userId,
        List<UUID> orderIds
) {}

