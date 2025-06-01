package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.OrderLineQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.OrderLineRepository;
import ucr.ac.cr.BackendVentas.jpa.repositories.ProductRepository;
import ucr.ac.cr.BackendVentas.models.OrderProduct;

import java.math.BigDecimal;
import java.util.List;

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

            line.setProduct(productEntity);
            line.setQuantity(product.quantity());
            line.setUnitPrice(productEntity.getPrice());
            line.setSubtotal(productEntity.getPrice().multiply(
                    BigDecimal.valueOf(product.quantity())
            ));
            orderLineQuery.save(line);
        }
    }
}
