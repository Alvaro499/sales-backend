package ucr.ac.cr.BackendVentas.handlers.commands;

import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

public interface ProductHandler {

    Result handle(Command command);

    sealed interface Result permits Result.Success, Result.InvalidFields, Result.PymeNotFound {
        record Success(ProductEntity product) implements Result {}
        record InvalidFields(String... fields) implements Result {}
        record PymeNotFound() implements Result {}
    }

    record Command(String name, String description, BigDecimal price, String category, List<String> images,
                   Boolean available, String promotion, Integer stock, Long pymeId) {}
}
