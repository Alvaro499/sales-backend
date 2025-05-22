package ucr.ac.cr.BackendVentas.api.types;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductRequest(
        String name,
        String description,
        BigDecimal price,
        List<String> category,
        List<String> images,
        Boolean available,
        String promotion,
        Integer stock,
        UUID pymeId
) {}
