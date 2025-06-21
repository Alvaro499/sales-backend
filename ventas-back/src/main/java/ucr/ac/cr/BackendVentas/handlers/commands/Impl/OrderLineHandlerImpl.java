package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.OrderLineQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.models.ErrorCode;
import ucr.ac.cr.BackendVentas.models.OrderProduct;
import ucr.ac.cr.BackendVentas.utils.MonetaryUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static ucr.ac.cr.BackendVentas.utils.ValidationUtils.validationError;

@Service
public class OrderLineHandlerImpl implements OrderLineHandler {

    private final OrderLineQuery orderLineQuery;
    private final ProductRepository productRepository;

    public OrderLineHandlerImpl(OrderLineQuery orderLineQuery, ProductRepository productRepository) {
        this.orderLineQuery = orderLineQuery;
        this.productRepository = productRepository;
    }

    //Aqui usamos OrderProduct (que debería llamarse OrderedProducts)

    @Override
    public List<OrderLineEntity> createOrderLines(OrderEntity order, List<OrderProduct> productsByOrder) {
        List<OrderLineEntity> lines = new ArrayList<>();

        for (OrderProduct product : productsByOrder) {
            OrderLineEntity line = buildOrderLine(order, product);
            OrderLineEntity savedLine = saveOrderLine(line);
            lines.add(savedLine);
        }

        return lines;
    }

    private OrderLineEntity buildOrderLine(OrderEntity order, OrderProduct product) {
        ProductEntity productEntity = productRepository.findById(product.productId())
                .orElseThrow(() -> validationError(
                        "Producto no encontrado: " + product.productId(),
                        ErrorCode.ENTITY_NOT_FOUND,
                        "product"
                ));

        BigDecimal originalPrice = productEntity.getPrice();
        BigDecimal promotion = productEntity.getPromotion() != null ? productEntity.getPromotion() : BigDecimal.ZERO;
        BigDecimal priceWithDiscount = MonetaryUtils.applyPromotion(originalPrice, promotion);
        BigDecimal subtotal = priceWithDiscount.multiply(BigDecimal.valueOf(product.quantity()));

        OrderLineEntity line = new OrderLineEntity();
        line.setOrder(order);
        line.setProduct(productEntity);
        line.setQuantity(product.quantity());
        line.setUnitPrice(originalPrice);
        line.setPromotionApplied(promotion);
        line.setPriceWithDiscount(priceWithDiscount);
        line.setSubtotal(subtotal);

        return line;
    }

    private OrderLineEntity saveOrderLine(OrderLineEntity line) {
        return orderLineQuery.save(line).orElseThrow(() -> validationError(
                "No se pudo guardar la línea de orden",
                ErrorCode.UNEXPECTED_ERROR,
                "orderLine"
        ));
    }

}