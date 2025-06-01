package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ucr.ac.cr.BackendVentas.api.types.enums.OrderStatus;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.commands.OrderLineHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.*;
import ucr.ac.cr.BackendVentas.jpa.entities.*;
import ucr.ac.cr.BackendVentas.models.OrderProduct;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CreateOrderHandlerImpl implements CreateOrderHandler {

    private final PaymentMethodQuery paymentMethodQuery;
    private final ShippingMethodQuery shippingMethodQuery;
    private final OrderQuery orderQuery;
    private final OrderLineHandler orderLineHandler;
    private final PymeQuery pymeQuery;
    private final ProductQuery productQuery;

    @Autowired
    public CreateOrderHandlerImpl(
            ProductQuery productQuery,
            PaymentMethodQuery paymentMethodQuery,
            ShippingMethodQuery shippingMethodQuery,
            OrderQuery orderQuery,
            PymeQuery pymeQuery,
            OrderLineHandlerImpl orderLineHandler
    ) {
        this.productQuery = productQuery;
        this.paymentMethodQuery = paymentMethodQuery;
        this.shippingMethodQuery = shippingMethodQuery;
        this.orderQuery = orderQuery;
        this.pymeQuery = pymeQuery;
        this.orderLineHandler = orderLineHandler;
    }

    @Transactional
    @Override
    public Result handle(Command command) {
        Map<PymeEntity, List<OrderProduct>> productsByPyme;

        try {
            productsByPyme = groupProductsByPyme(command.products());
        } catch (NoSuchElementException e) {
            return new Result.InvalidFields("Problemas con Lista de Productos: " + e, new String[]{"products"});
        }

        Result validationResult = validateAll(command, productsByPyme);
        if (validationResult != null) return validationResult;

        //try-catch para manejar el rollback entre Orders y OrderLines
        try {
            List<OrderEntity> orders = createOrders(command, productsByPyme);
            return new Result.Success(orders.stream().map(OrderEntity::getId).toList());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new Result.InvalidFields("Error creando órdenes: " + e.getMessage());
        }
    }


    private Result validateAll(Command command, Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        Result emailValidation = validateEmail(command.email());
        if (emailValidation != null) return emailValidation;

        Result nameValidation = validateName("firstName", command.firstName());
        if (nameValidation != null) return nameValidation;

        Result lastNameValidation = validateName("lastName", command.lastName());
        if (lastNameValidation != null) return lastNameValidation;

        Result phoneValidation = validatePhone(command.phone());
        if (phoneValidation != null) return phoneValidation;

        Result addressValidation = validateShippingAddress(command.shippingAddress());
        if (addressValidation != null) return addressValidation;

        Result paymentMethodValidation = validatePaymentMethod(command.paymentMethod());
        if (paymentMethodValidation != null) return paymentMethodValidation;

        Result shippingMethodValidation = validateShippingMethod(command.shippingMethod());
        if (shippingMethodValidation != null) return shippingMethodValidation;

        Result pymesValidation = validatePymes(productsByPyme.keySet());
        if (pymesValidation != null) return pymesValidation;

        Result productValidation = validateProducts(productsByPyme);
        if (productValidation != null) return productValidation;

        Result stockValidation = validateStock(productsByPyme);
        return stockValidation;
    }

    public List<OrderEntity> createOrders(Command command, Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        List<OrderEntity> orders = new ArrayList<>();

        Optional<PaymentMethodEntity> paymentMethodEntity = paymentMethodQuery.findByName(command.paymentMethod());
        Optional<ShippingMethodEntity> shippingMethodEntity = shippingMethodQuery.findByName(command.shippingMethod());

        for (Map.Entry<PymeEntity, List<OrderProduct>> entry : productsByPyme.entrySet()) {
            PymeEntity pyme = entry.getKey();
            List<OrderProduct> products = entry.getValue();

            OrderEntity newOrder = new OrderEntity();
            newOrder.setUser(command.userId());
            newOrder.setPyme(pyme);
            newOrder.setStatus(OrderStatus.PENDIENTE);
            newOrder.setShippingAddress(command.shippingAddress());
            newOrder.setPaymentMethod(paymentMethodEntity.get()); //ya se validó el método de pago en ValidateAll()
            newOrder.setShippingMethod(shippingMethodEntity.get()); //ya se validó el método de entrega en ValidateAll()
            newOrder.setTotalAmount(calculateTotalAmount(products));

            // Guardar la orden
            Optional<OrderEntity> savedOrder = orderQuery.save(newOrder);

            // Crear líneas de orden
            orderLineHandler.createOrderLines(savedOrder.get(), products);

            orders.add(newOrder);
        }
        return orders;
    }

    //Agrupar productos por PYME
    private Map<PymeEntity, List<OrderProduct>> groupProductsByPyme(List<OrderProduct> products) {
        Map<PymeEntity, List<OrderProduct>> groupedProductsByPyme = new HashMap<>();

        for (OrderProduct product : products) {
            ProductEntity productEntity = productQuery.findById(product.productId())
                    .orElseThrow();

            groupedProductsByPyme.computeIfAbsent(productEntity.getPyme(), k -> new ArrayList<>()).add(product);
        }
        return groupedProductsByPyme;
    }

    //Recibe únicamente la lista de products de la PYME indicada dentro del for dentro del metodo createOrders
    private BigDecimal calculateTotalAmount(List<OrderProduct> orderProducts) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderProduct orderProduct : orderProducts) {
            UUID productId = orderProduct.productId();
            int quantity = orderProduct.quantity();

            ProductEntity product = productQuery.findById(productId).get();
            BigDecimal price = product.getPrice();

            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);
        }
        return total;
    }

    private Result validateEmail(String email) {
        if (email == null) {
            return new Result.InvalidFields("El email no puede ser nulo", "email");
        }
        if (email.isBlank()) {
            return new Result.InvalidFields("El email no puede estar vacío", "email");
        }
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return new Result.InvalidFields("El formato del email es inválido", "email");
        }
        return null;
    }

    private Result validateName(String fieldName, String name) {
        if (name == null) {
            return new Result.InvalidFields("El campo " + fieldName + " no puede ser nulo", fieldName);
        }
        if (name.isBlank()) {
            return new Result.InvalidFields("El campo " + fieldName + " no puede estar vacío", fieldName);
        }
        if (name.length() > 100) {
            return new Result.InvalidFields("El campo " + fieldName + " no puede tener más de 100 caracteres", fieldName);
        }
        return null;
    }

    private Result validatePhone(String phone) {
        if (phone == null) {
            return new Result.InvalidFields("El teléfono no puede ser nulo", "phone");
        }
        if (phone.isBlank()) {
            return new Result.InvalidFields("El teléfono no puede estar vacío", "phone");
        }
        if (!phone.matches("[\\d-]{7,15}")) {
            return new Result.InvalidFields("El formato del teléfono es inválido", "phone");
        }
        return null;
    }

    //direccion exacta
    private Result validateShippingAddress(String address) {
        if (address == null) {
            return new Result.InvalidFields("La dirección de envío no puede ser nula", "shippingAddress");
        }
        if (address.isBlank()) {
            return new Result.InvalidFields("La dirección de envío no puede estar vacía", "shippingAddress");
        }
        if (address.length() > 200) {
            return new Result.InvalidFields("La dirección de envío no puede tener más de 200 caracteres", "shippingAddress");
        }
        return null;
    }


    private Result validatePaymentMethod(String method) {
        if (method == null) {
            return new Result.InvalidFields("El método de pago no puede ser nulo", "paymentMethod");
        }
        if (method.isBlank()) {
            return new Result.InvalidFields("El método de pago no puede estar vacío", "paymentMethod");
        }
        boolean existsAndActive = paymentMethodQuery.existsByNameIgnoreCaseAndIsActiveTrue(method);
        if (!existsAndActive) {
            return new Result.InvalidFields("El método de pago no es válido o no está activo", "paymentMethod");
        }
        return null;
    }

    //método de envío
    private Result validateShippingMethod(String method) {
        if (method == null){
            return new Result.InvalidFields("El método de envío no puede ser nulo", "shippingMethod");
        }

        if (method.isBlank()) {
            return new Result.InvalidFields("El método de envío no puede estar vacío","shippingMethod");
        }

        boolean existsAndActive = shippingMethodQuery.existsByNameIgnoreCaseAndIsActiveTrue(method);
        if (!existsAndActive) {
            return new Result.InvalidFields("El método de envío no es válido o no está activo","shippingMethod");
        }
        return null;
    }

    //Se usa el DTO "OrderProduct de Models"
    private Result validateProducts(Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        for (Map.Entry<PymeEntity, List<OrderProduct>> entry : productsByPyme.entrySet()) {
            for (OrderProduct orderedProduct : entry.getValue()) {

                if (orderedProduct.productId() == null) {
                    return new Result.InvalidFields("El ID del producto es obligatorio", "products");
                }

                if (orderedProduct.quantity() <= 0) {
                    return new Result.InvalidFields("La cantidad debe ser mayor que cero", "products");
                }

                Optional<ProductEntity> foundProduct = productQuery.findById(orderedProduct.productId());
                if (foundProduct.isEmpty()) {
                    return new Result.NotFound("Producto no encontrado: " + orderedProduct.productId());
                }

                ProductEntity productEntity = foundProduct.get();

                if (!productEntity.isActive() || !productEntity.getAvailable()) {
                    return new Result.InvalidFields(
                            "El producto '" + productEntity.getName() + "' no está disponible",
                            "products"
                    );
                }
            }
        }
        return null;
    }

    //Se usa el DTO "OrderProduct de Models"
    private Result validateStock(Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        for (Map.Entry<PymeEntity, List<OrderProduct>> entry : productsByPyme.entrySet()) {
            for (OrderProduct orderedProduct : entry.getValue()) {

                if (orderedProduct.quantity() <= 0) {
                    return new Result.InvalidFields("La cantidad debe ser mayor que cero", "products");
                }

                Optional<ProductEntity> foundProduct = productQuery.findById(orderedProduct.productId());
                if (foundProduct.isEmpty()) {
                    return new Result.NotFound("Producto no encontrado: " + orderedProduct.productId());
                }

                ProductEntity product = foundProduct.get();
                int availableStock = product.getStock();

                if (orderedProduct.quantity() > availableStock) {
                    return new Result.InvalidFields(
                            "El producto '" + product.getName() + "' no tiene suficiente stock. " +
                                    "Pedido: " + orderedProduct.quantity() + ", Disponible: " + availableStock,
                            "products"
                    );
                }
            }
        }
        return null;
    }

    private Result validatePymes(Set<PymeEntity> pymes) {
        for (PymeEntity pyme : pymes) {

            if (pyme == null) {
                return new Result.InvalidFields("Una de las pymes asociadas es nula", "pymes");
            }

            Optional<PymeEntity> foundPyme = pymeQuery.findById(pyme.getId());
            if (foundPyme.isEmpty()) {
                return new Result.InvalidFields("La pyme con ID " + pyme.getId() + " no existe en la base de datos", "pymes");
            }

            if (!foundPyme.get().getActive()) {
                return new Result.InvalidFields("La pyme " + foundPyme.get().getName() + " está inactiva", "pymes");
            }
        }
        return null;
    }

}