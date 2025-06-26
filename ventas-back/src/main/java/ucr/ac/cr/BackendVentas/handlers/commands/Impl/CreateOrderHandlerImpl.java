package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucr.ac.cr.BackendVentas.api.types.enums.OrderStatus;
import ucr.ac.cr.BackendVentas.events.ProductSendDTO;
import ucr.ac.cr.BackendVentas.events.PurchaseSummaryMessage;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateClientHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.*;
import ucr.ac.cr.BackendVentas.jpa.entities.*;
import ucr.ac.cr.BackendVentas.models.ErrorCode;
import ucr.ac.cr.BackendVentas.models.OrderProduct;
import ucr.ac.cr.BackendVentas.handlers.validators.OrderValidator;
import ucr.ac.cr.BackendVentas.producers.PurchaseSummaryProducer;
import ucr.ac.cr.BackendVentas.service.PurchaseSummaryAssembler;
import ucr.ac.cr.BackendVentas.service.SendPurchaseService;
import ucr.ac.cr.BackendVentas.utils.MonetaryUtils;
import ucr.ac.cr.BackendVentas.utils.ValidationUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static ucr.ac.cr.BackendVentas.utils.ValidationUtils.validationError;

@Service
public class CreateOrderHandlerImpl implements CreateOrderHandler {

    private final CreateClientHandler createClientHandler;
    private final OrderLineHandler orderLineHandler;

    private final ClientQuery clientQuery;
    private final OrderQuery orderQuery;
    private final ProductQuery productQuery;
    private final OrderValidator orderValidator;
    private final PaymentMethodQuery paymentMethodQuery;
    private final ShippingMethodQuery shippingMethodQuery;
    private final PurchaseSummaryProducer purchaseSummaryProducer;
    private final SendPurchaseService sendPurchaseService;


    public CreateOrderHandlerImpl(CreateClientHandler createClientHandler,
                                  OrderQuery orderQuery,
                                  ProductQuery productQuery,
                                  OrderLineHandler orderLineHandler,
                                  OrderValidator orderValidator,
                                  PaymentMethodQuery paymentMethodQuery,
                                  ShippingMethodQuery shippingMethodQuery,
                                  PurchaseSummaryProducer purchaseSummaryProducer,
                                  SendPurchaseService sendPurchaseService,
                                  ClientQuery clientQuery) {
        this.createClientHandler = createClientHandler;
        this.orderQuery = orderQuery;
        this.productQuery = productQuery;
        this.orderLineHandler = orderLineHandler;
        this.orderValidator = orderValidator;
        this.paymentMethodQuery = paymentMethodQuery;
        this.shippingMethodQuery = shippingMethodQuery;
        this.purchaseSummaryProducer = purchaseSummaryProducer;
        this.sendPurchaseService = sendPurchaseService;
        this.clientQuery = clientQuery;
    }

    @Transactional
    @Override
    public Result handle(Command command) {

        //Reconocemos el tipo de UUID que entra
        Command finalCommand = prepareCommandWithBuyerId(command);

        Map<PymeEntity, List<OrderProduct>> productsByPyme = groupProductsByPyme(finalCommand.products());
        validateAll(finalCommand, productsByPyme);
        List<OrderEntity> orders = createOrders(finalCommand, productsByPyme);

        //Recolectar los datos creados durante la creación de órdenes para enviar
        //el mensaje de resumen de compra por email
        Map<UUID, PymeEntity> pymesMap = productsByPyme.keySet().stream()
                .collect(Collectors.toMap(PymeEntity::getId, p -> p));

        PurchaseSummaryMessage message = PurchaseSummaryAssembler.toMessage(
                finalCommand,
                orders,
                pymesMap
        );

        purchaseSummaryProducer.sendEmailSummary(message);

        // Enviar productos comprados por Kafka
        List<ProductSendDTO.ProductInfo> productList = command.products().stream().map(p -> {
            ProductEntity prod = productQuery.findById(p.productId()).orElseThrow();
            return new ProductSendDTO.ProductInfo(prod.getId(), prod.getName());
        }).toList();

        ProductSendDTO kafkaMessage = new ProductSendDTO(
                finalCommand.userId(),
                finalCommand.firstName() + " " + finalCommand.lastName(),
                productList
        );

        sendPurchaseService.sendOrder(kafkaMessage);

        //Se retornan los IDs de las órdenes creadas
        List<UUID> orderIds = orders.stream()
                .map(OrderEntity::getId)
                .toList();
        return new Result.Success(orderIds, finalCommand.userId());
    }

    private void validateAll(Command command, Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        ValidationUtils.validateEmail(command.email());
        ValidationUtils.validateName("firstName", command.firstName());
        ValidationUtils.validateName("lastName", command.lastName());
        ValidationUtils.validatePhone(command.phone());
        ValidationUtils.validateShippingAddress(command.shippingAddress());
        orderValidator.validateBuyer(command.buyerType(), command.userId());
        orderValidator.validatePaymentMethod(command.paymentMethod());
        orderValidator.validateShippingMethod(command.shippingMethod());
        orderValidator.validatePymes(productsByPyme.keySet());
        orderValidator.validateProducts(productsByPyme);
        orderValidator.validateStock(productsByPyme);
    }

    private Command prepareCommandWithBuyerId(Command original) {
        UUID userId = original.userId();

        if ("CLIENT".equalsIgnoreCase(original.buyerType())) {
            userId = createClientHandler.handle(new CreateClientHandler.Command(userId));
        }

        return new Command(
                userId,
                original.buyerType(),
                original.email(),
                original.firstName(),
                original.lastName(),
                original.phone(),
                original.shippingAddress(),
                original.paymentMethod(),
                original.shippingMethod(),
                original.products()
        );
    }

    private OrderEntity buildOrderForPyme(
            Command command,
            PymeEntity pyme,
            List<OrderProduct> products,
            PaymentMethodEntity paymentMethod,
            ShippingMethodEntity shippingMethod
    ) {
        OrderEntity newOrder = new OrderEntity();

        if ("CLIENT".equalsIgnoreCase(command.buyerType())) {
            ClientEntity client = clientQuery.findById(command.userId()).get();
            newOrder.setClient(client);
        } else {
            newOrder.setUser(command.userId());
        }

        newOrder.setPyme(pyme);
        newOrder.setStatus(OrderStatus.PENDIENTE);
        newOrder.setShippingAddress(command.shippingAddress());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setShippingMethod(shippingMethod);
        newOrder.setTotalAmount(calculateOrderTotalAmount(products));

        return newOrder;
    }

    private List<OrderEntity> createOrders(Command command, Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        List<OrderEntity> orders = new ArrayList<>();

        PaymentMethodEntity paymentMethod = paymentMethodQuery.findByName(command.paymentMethod()).orElseThrow();
        ShippingMethodEntity shippingMethod = shippingMethodQuery.findByName(command.shippingMethod()).orElseThrow();

        for (Map.Entry<PymeEntity, List<OrderProduct>> entry : productsByPyme.entrySet()) {
            PymeEntity pyme = entry.getKey();
            List<OrderProduct> products = entry.getValue();

            OrderEntity newOrder = buildOrderForPyme(command, pyme, products, paymentMethod, shippingMethod);
            OrderEntity savedOrder = orderQuery.save(newOrder).get();

            List<OrderLineEntity> associatedLines = orderLineHandler.createOrderLines(savedOrder, products);
            savedOrder.setOrderLines(associatedLines);

            orders.add(savedOrder);
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