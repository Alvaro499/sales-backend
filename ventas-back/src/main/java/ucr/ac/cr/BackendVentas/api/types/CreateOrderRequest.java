package ucr.ac.cr.BackendVentas.api.types;

import ucr.ac.cr.BackendVentas.models.OrderProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    UUID guestUserId,
    String buyerType, // "USER" o "CLIENT"
    String email,           
    String firstName,
    String lastName,
    String phone,
    String shippingAddress,
    List<OrderProduct> products,
    String shippingMethod,
    String paymentMethod
) {

}