package ucr.ac.cr.BackendVentas.handlers.commands;

import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductHandler {

    Result handle(Command command);

    Result listProductsByPyme(UUID pymeId);

    Result unpublishProduct(ProductHandler.UnpublishProductCommand command);

    Result changeAvailabilityAndStock(ProductHandler.ChangeAvailabilityAndStockCommand command);

    Result searchProducts(String term, Integer categoryId, BigDecimal priceMin, BigDecimal priceMax);

    Result listAllProducts();

    Result applyPromotion(ApplyPromotionCommand command);


    // Command for creating a product
    record Command(String name, String description, BigDecimal price, List<String> category, List<String> images,
                   Boolean available, String promotion, Integer stock, UUID pymeId) {}

    // Command for unpublishing a product
    record UnpublishProductCommand(UUID productId) {}

    // Command for applying promotion to a product
    record ApplyPromotionCommand(UUID productId, String promotion) {}

    // Command for changing availability and stock of a product
    record ChangeAvailabilityAndStockCommand(UUID productId, Boolean available, Integer stock) {}

    // Command to list products by pymeId
    record ListProductsByPymeCommand(UUID pymeId) {}


    sealed interface Result permits Result.Success, Result.InvalidFields, Result.PymeNotFound, Result.NotFoundProduct, Result.SuccessList, Result.PromotionInvalid {
        record Success(ProductEntity product) implements Result {}
        record SuccessList(List<ProductEntity> products) implements Result {}
        record InvalidFields(String... fields) implements Result {}
        record PymeNotFound() implements Result {}
        record NotFoundProduct() implements Result {}  // New result for product not found
        record PromotionInvalid(String... fields) implements Result{}
    }


}
