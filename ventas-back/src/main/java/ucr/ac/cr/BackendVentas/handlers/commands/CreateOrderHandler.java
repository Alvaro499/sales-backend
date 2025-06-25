package ucr.ac.cr.BackendVentas.handlers.commands;

import java.util.List;
import java.util.UUID;
import ucr.ac.cr.BackendVentas.models.OrderProduct;


public interface CreateOrderHandler {

    Result handle(Command command);

    sealed interface Result permits Result.Success, Result.InvalidFields, Result.OutOfStock, Result.NotFound {
        record Success(List<UUID> orderIds, UUID userId) implements Result {}
        record InvalidFields(String msg ,String... fields) implements Result {}
        record OutOfStock(UUID productId, int requestedQuantity, int availableStock) implements Result {}
        record NotFound(String message) implements Result {}
    }

    record Command(
            // puede ser userId real o guestUserId
            UUID userId,
            String buyerType, // nuevo
            String email,
            String firstName,
            String lastName,
            String phone,
            String shippingAddress,
            String paymentMethod,
            String shippingMethod,
            List<OrderProduct> products
    ) {}
}

