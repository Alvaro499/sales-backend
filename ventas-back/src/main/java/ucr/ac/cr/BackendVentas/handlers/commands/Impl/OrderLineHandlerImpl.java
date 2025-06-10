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
    public void createOrderLines(OrderEntity order, List<OrderProduct> productsByOrder) {
        for (OrderProduct product : productsByOrder) {
            OrderLineEntity line = new OrderLineEntity();
            line.setOrder(order);

            ProductEntity productEntity = productRepository.findById(product.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));


            //BigDecimal unitPrice = applyPromotion(productEntity);
            //BigDecimal subtotal = MonetaryUtils.round(
                    //unitPrice.multiply(BigDecimal.valueOf(product.quantity()))
            //);

            line.setProduct(productEntity);
            line.setQuantity(product.quantity());
            line.setUnitPrice(productEntity.getPrice());
            line.setSubtotal(productEntity.getPrice().multiply(
                    BigDecimal.valueOf(product.quantity())
            ));
            orderLineQuery.save(line);
        }
    }

    /**
     * La promoción se maneja entre valores de 0 y 1, donde 0 es sin descuento y 0.9
     * es un descuento del 90%.
     * La fórmula es: finalPrice = price × (1 - promotion)
     * Si la promoción es 0.20 → discountFactor = 1 - 0.20 = 0.80
     */

    private BigDecimal applyPromotion(ProductEntity product) {
        BigDecimal price = product.getPrice();
        BigDecimal promotion = product.getPromotion();

        if (promotion == null) promotion = BigDecimal.ZERO;

        boolean isNegativePromotion = promotion.compareTo(BigDecimal.ZERO) < 0;
        boolean isOverNinetyPercent = promotion.compareTo(new BigDecimal("0.90")) > 0;

        if (isNegativePromotion || isOverNinetyPercent) {
            throw validationError(
                    "Promoción inválida para el producto: " + product.getId(),
                    ErrorCode.INVALID_FORMAT,
                    "promotion"
            );
        }
        BigDecimal discountFactor = BigDecimal.ONE.subtract(promotion);
        BigDecimal finalPrice = price.multiply(discountFactor);

        //Redondeo seguro a 2 decimales
        return MonetaryUtils.round(finalPrice);
    }

}
