package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.BackendVentas.api.types.enums.OrderStatus;
import ucr.ac.cr.BackendVentas.events.PurchaseSummaryMessage;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.OrderQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.PaymentMethodQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ProductQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ShippingMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.*;
import ucr.ac.cr.BackendVentas.models.ErrorCode;
import ucr.ac.cr.BackendVentas.models.OrderProduct;
import ucr.ac.cr.BackendVentas.handlers.validators.OrderValidator;
import ucr.ac.cr.BackendVentas.producers.PurchaseSummaryProducer;
import ucr.ac.cr.BackendVentas.service.PurchaseSummaryAssembler;
import ucr.ac.cr.BackendVentas.utils.MonetaryUtils;
import ucr.ac.cr.BackendVentas.utils.ValidationUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static ucr.ac.cr.BackendVentas.utils.ValidationUtils.validationError;

@Service
public class CreateOrderHandlerImpl implements CreateOrderHandler {

    private final OrderQuery orderQuery;
    private final ProductQuery productQuery;
    private final OrderLineHandler orderLineHandler;
    private final OrderValidator orderValidator;
    private final PaymentMethodQuery paymentMethodQuery;
    private final ShippingMethodQuery shippingMethodQuery;
    private final PurchaseSummaryProducer purchaseSummaryProducer;

    public CreateOrderHandlerImpl(OrderQuery orderQuery,
                                  ProductQuery productQuery,
                                  OrderLineHandler orderLineHandler,
                                  OrderValidator orderValidator,
                                  PaymentMethodQuery paymentMethodQuery,
                                  ShippingMethodQuery shippingMethodQuery,
                                  PurchaseSummaryProducer purchaseSummaryProducer) {

        this.orderQuery = orderQuery;
        this.productQuery = productQuery;
        this.orderLineHandler = orderLineHandler;
        this.orderValidator = orderValidator;
        this.paymentMethodQuery = paymentMethodQuery;
        this.shippingMethodQuery = shippingMethodQuery;
        this.purchaseSummaryProducer = purchaseSummaryProducer;
    }

    @Transactional
    @Override
    public Result handle(Command command) {

        Map<PymeEntity, List<OrderProduct>> productsByPyme = groupProductsByPyme(command.products());
        validateAll(command, productsByPyme);
        List<OrderEntity> orders = createOrders(command, productsByPyme);

        //Recolectar los datos creados durante la creación de órdenes para enviar
        //el mensaje de resumen de compra por email
        Map<UUID, PymeEntity> pymesMap = productsByPyme.keySet().stream()
                .collect(Collectors.toMap(PymeEntity::getId, p -> p));

        PurchaseSummaryMessage message = PurchaseSummaryAssembler.toMessage(
                command,
                orders,
                pymesMap
        );

        purchaseSummaryProducer.sendEmailSummary(message);
        //Se retornan los IDs de las órdenes creadas
        return new Result.Success(orders.stream().map(OrderEntity::getId).toList());
    }

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
            newOrder.setTotalAmount(calculateOrderTotalAmount(products));

            Optional<OrderEntity> savedOrder = orderQuery.save(newOrder);
            List<OrderLineEntity> associatedLines = orderLineHandler.createOrderLines(savedOrder.get(), products);
            orderLineHandler.createOrderLines(savedOrder.get(), products);
            savedOrder.get().setOrderLines(associatedLines);

            orders.add(newOrder);
        }
        return orders;
    }

    private BigDecimal calculateOrderTotalAmount(List<OrderProduct> orderProducts) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderProduct orderProduct : orderProducts) {
            UUID productId = orderProduct.productId();
            int quantity = orderProduct.quantity();

            ProductEntity product = productQuery.findById(productId).orElseThrow();
            BigDecimal finalPrice = MonetaryUtils.applyPromotion(product.getPrice(), product.getPromotion());
            total = total.add(finalPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        return total;
    }

    private Map<PymeEntity, List<OrderProduct>> groupProductsByPyme(List<OrderProduct> products) {
        Map<PymeEntity, List<OrderProduct>> grouped = new HashMap<>();

        for (OrderProduct product : products) {

            Optional<ProductEntity> optionalProduct = productQuery.findById(product.productId());
            if (optionalProduct.isEmpty()) {
                throw validationError(
                        "Producto no encontrado: " + product.productId(),
                        ErrorCode.ENTITY_NOT_FOUND,
                        "products"
                );
            }

            ProductEntity productEntity = optionalProduct.get();
            grouped.computeIfAbsent(productEntity.getPyme(), k -> new ArrayList<>()).add(product);
        }

        return grouped;
    }
}