package ucr.ac.cr.BackendVentas.events;

import java.util.List;
import java.util.UUID;

public record ProductSendDTO(
        UUID userId,
        String userName,
        List<ProductInfo> products
) {
    public record ProductInfo(UUID productId, String name) {}
}
