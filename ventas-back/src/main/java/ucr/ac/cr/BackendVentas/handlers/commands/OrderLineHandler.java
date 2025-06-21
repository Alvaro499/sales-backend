package ucr.ac.cr.BackendVentas.handlers.commands;

import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.models.OrderProduct;

import java.util.List;
import java.util.Map;

public interface OrderLineHandler {
    //void createOrderLines(OrderEntity order, List<OrderProduct> productsByOrder);
    List<OrderLineEntity> createOrderLines(OrderEntity order, List<OrderProduct> productsByOrder);
}
