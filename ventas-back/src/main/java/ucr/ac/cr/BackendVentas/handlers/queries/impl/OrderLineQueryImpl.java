package ucr.ac.cr.BackendVentas.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.queries.OrderLineQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.OrderLineEntity;
import ucr.ac.cr.BackendVentas.jpa.repositories.OrderLineRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderLineQueryImpl implements OrderLineQuery {

    @Autowired
    private OrderLineRepository orderLineRepository;

    @Override
    public Optional<OrderLineEntity> findById(UUID uuid) {
        return orderLineRepository.findById(uuid);
    }

    @Override
    public Optional<OrderLineEntity> save(OrderLineEntity line) {
        return Optional.of(orderLineRepository.save(line));
    }
}