package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.BackendVentas.api.types.enums.OrderStatus;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.OrderQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.PaymentMethodQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ProductQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ShippingMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.*;
import ucr.ac.cr.BackendVentas.models.OrderProduct;
import ucr.ac.cr.BackendVentas.handlers.validators.OrderValidator;
import ucr.ac.cr.BackendVentas.utils.ValidationUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CreateOrderHandlerImpl implements CreateOrderHandler {

    private final OrderQuery orderQuery;
    private final ProductQuery productQuery;
    private final OrderLineHandler orderLineHandler;
    private final OrderValidator orderValidator;
    private final PaymentMethodQuery paymentMethodQuery;
    private final ShippingMethodQuery shippingMethodQuery;

    public CreateOrderHandlerImpl(OrderQuery orderQuery,
                                  ProductQuery productQuery,
                                  OrderLineHandler orderLineHandler,
                                  OrderValidator orderValidator,
                                  PaymentMethodQuery paymentMethodQuery,
                                  ShippingMethodQuery shippingMethodQuery) {

        this.orderQuery = orderQuery;
        this.productQuery = productQuery;
        this.orderLineHandler = orderLineHandler;
        this.orderValidator = orderValidator;
        this.paymentMethodQuery = paymentMethodQuery;
        this.shippingMethodQuery = shippingMethodQuery;
    }

    @Transactional
    @Override
    public Result handle(Command command) {

        Map<PymeEntity, List<OrderProduct>> productsByPyme = groupProductsByPyme(command.products());

        validateAll(command, productsByPyme);

        List<OrderEntity> orders = createOrders(command, productsByPyme);

        return new Result.Success(orders.stream().map(OrderEntity::getId).toList());
    }

    /** Revisarlo
     * Este método mapea una BaseException a un Result específico.
     * lo que permite seguir usando los Result para mantener los switch
     * en el controlador y no en el manejador de excepciones global.

    private Result mapBaseExceptionToResult(BaseException ex) {
        return switch (ErrorCode.valueOf(ex.getCode())) {
            case REQUIRED_FIELDS, INVALID_FORMAT ->
                    new Result.InvalidField(ex.getMessage(), ex.getParams().isEmpty() ? null : ex.getParams().get(0));
            case ENTITY_NOT_FOUND -> new Result.NotFound(ex.getMessage());
            case CONFLICT -> new Result.OutOfStock(ex.getMessage());
            case UNAUTHORIZED -> new Result.InvalidField(ex.getMessage(), "unauthorized");
        };
    }
    */

    private void validateAll(Command command, Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        ValidationUtils.validateEmail(command.email());
        ValidationUtils.validateName("firstName", command.firstName());
        ValidationUtils.validateName("lastName", command.lastName());
        ValidationUtils.validatePhone(command.phone());
        ValidationUtils.validateShippingAddress(command.shippingAddress());
        orderValidator.validatePaymentMethod(command.paymentMethod());
        orderValidator.validateShippingMethod(command.shippingMethod());
        orderValidator.validatePymes(productsByPyme.keySet());
        orderValidator.validateProducts(productsByPyme);
        orderValidator.validateStock(productsByPyme);
    }

    private List<OrderEntity> createOrders(Command command, Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        List<OrderEntity> orders = new ArrayList<>();

        Optional<PaymentMethodEntity> paymentMethod = paymentMethodQuery.findByName(command.paymentMethod());
        Optional<ShippingMethodEntity> shippingMethod = shippingMethodQuery.findByName(command.shippingMethod());

        for (Map.Entry<PymeEntity, List<OrderProduct>> entry : productsByPyme.entrySet()) {
            PymeEntity pyme = entry.getKey();
            List<OrderProduct> products = entry.getValue();

            OrderEntity newOrder = new OrderEntity();
            newOrder.setUser(command.userId());
            newOrder.setPyme(pyme);
            newOrder.setStatus(OrderStatus.PENDIENTE);
            newOrder.setShippingAddress(command.shippingAddress());
            newOrder.setPaymentMethod(paymentMethod.get());
            newOrder.setShippingMethod(shippingMethod.get());
            newOrder.setTotalAmount(calculateTotalAmount(products));

            Optional<OrderEntity> savedOrder = orderQuery.save(newOrder);
            orderLineHandler.createOrderLines(savedOrder.get(), products);

            orders.add(newOrder);
        }

        return orders;
    }

    private BigDecimal calculateTotalAmount(List<OrderProduct> orderProducts) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderProduct orderProduct : orderProducts) {
            UUID productId = orderProduct.productId();
            int quantity = orderProduct.quantity();

            ProductEntity product = productQuery.findById(productId).orElseThrow();
            BigDecimal price = product.getPrice();

            total = total.add(price.multiply(BigDecimal.valueOf(quantity)));
        }

        return total;
    }

    private Map<PymeEntity, List<OrderProduct>> groupProductsByPyme(List<OrderProduct> products) {
        Map<PymeEntity, List<OrderProduct>> grouped = new HashMap<>();

        for (OrderProduct product : products) {
            ProductEntity productEntity = productQuery.findById(product.productId()).orElseThrow();
            grouped.computeIfAbsent(productEntity.getPyme(), k -> new ArrayList<>()).add(product);
        }

        return grouped;
    }
}