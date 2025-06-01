package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.OrderQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.OrderRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderQueryImpl implements OrderQuery {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<OrderEntity> findById(UUID uuid) {
        return orderRepository.findById(uuid);
    }

    @Override
    public Optional<OrderEntity> save(OrderEntity line) {
        return Optional.of(orderRepository.save(line));
    }
}