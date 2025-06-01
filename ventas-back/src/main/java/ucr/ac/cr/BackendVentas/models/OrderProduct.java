package ucr.ac.cr.BackendVentas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderProduct(
        @JsonProperty("productId") UUID productId,
        @JsonProperty("quantity") int quantity
) {}