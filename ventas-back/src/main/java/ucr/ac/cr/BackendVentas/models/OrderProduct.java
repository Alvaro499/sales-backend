package ucr.ac.cr.BackendVentas.models;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderProduct(
        UUID productId,
        int quantity
) {}
