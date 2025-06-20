package ucr.ac.cr.BackendVentas.service;

import ucr.ac.cr.BackendVentas.events.PurchaseSummaryMessage;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * request: los datos originales del cliente (nombre, correo, etc.)
 *
 * orders: las órdenes ya creadas en base de datos.
 *
 * pymes: un Map para acceder rápido a cada pyme por su UUID.
 */

public class PurchaseSummaryAssembler {
    public static PurchaseSummaryMessage toMessage(
            CreateOrderHandler.Command request,
            List<OrderEntity> orders,
            Map<UUID, PymeEntity> pymes
    ) {

        List<PurchaseSummaryMessage.PymeOrder> pymeOrders = new ArrayList<>();

        for (OrderEntity order : orders) {
            //Obtener la pyme asociada a la orden
            PymeEntity pyme = pymes.get(order.getPyme().getId());

            // 2. Construir el PymeOrder para esta orden
            PurchaseSummaryMessage.PymeOrder pymeOrder = buildPymeOrder(order, pyme);

            // 3. Agregarlo a la lista
            pymeOrders.add(pymeOrder);
        }

        return new PurchaseSummaryMessage(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.shippingAddress(),
                request.shippingMethod(),
                request.paymentMethod(),
                calculateGrandTotal(orders),
                pymeOrders
        );
    }

    private static BigDecimal calculateGrandTotal(List<OrderEntity> orders) {
        return orders.stream()
                .map(OrderEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static PurchaseSummaryMessage.PymeOrder buildPymeOrder(OrderEntity order, PymeEntity pyme) {
        List<PurchaseSummaryMessage.Product> products = order.getOrderLines().stream()
                .map(PurchaseSummaryAssembler::buildProduct)
                .toList();

        return new PurchaseSummaryMessage.PymeOrder(
                pyme.getEmail(),
                pyme.getName(),
                order.getTotalAmount(),
                products
        );
    }

    private static PurchaseSummaryMessage.Product buildProduct(OrderLineEntity line) {
        return new PurchaseSummaryMessage.Product(
                line.getProduct().getName(),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getPriceWithDiscount(),
                line.getPromotionApplied(),
                line.getSubtotal()
        );
    }
}