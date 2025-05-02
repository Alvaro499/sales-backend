package ucr.ac.cr.BackendVentas.handlers.commands;

import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductHandler {

    Result handle(Command command);

    sealed interface Result permits Result.Success, Result.InvalidFields, Result.PymeNotFound, Result.NotFoundProduct {
        record Success(ProductEntity product) implements Result {}
        record InvalidFields(String... fields) implements Result {}
        record PymeNotFound() implements Result {}
        record NotFoundProduct() implements Result {}  // New result for product not found
    }

    // Command for creating a product
    record Command(String name, String description, BigDecimal price, String category, List<String> images,
                   Boolean available, String promotion, Integer stock, UUID pymeId) {}

    // Command for unpublishing a product
    record UnpublishProductCommand(UUID productId) {}

    // Command for applying promotion to a product
    record ApplyPromotionCommand(UUID productId, String promotion) {}

    // Command for changing availability and stock of a product
    record ChangeAvailabilityAndStockCommand(UUID productId, Boolean available, Integer stock) {}
}
