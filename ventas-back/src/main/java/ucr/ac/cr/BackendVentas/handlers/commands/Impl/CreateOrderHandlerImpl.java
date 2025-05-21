package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.PaymentMethodQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ProductQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ShippingMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.jpa.entities.PymeEntity;
import ucr.ac.cr.BackendVentas.models.OrderProduct;

import java.util.*;

@Service
public class CreateOrderHandlerImpl implements CreateOrderHandler {

    private final ProductQuery productQuery;
    private final PaymentMethodQuery paymentMethodQuery;
    private final ShippingMethodQuery shippingMethodQuery;

    @Autowired
    public CreateOrderHandlerImpl(
            ProductQuery productQuery,
            PaymentMethodQuery paymentMethodQuery,
            ShippingMethodQuery shippingMethodQuery
    ) {
        this.productQuery = productQuery;
        this.paymentMethodQuery = paymentMethodQuery;
        this.shippingMethodQuery = shippingMethodQuery;
    }

    @Override
    public Result handle(Command command) {

        //Como el carrito trae productos de varias pymes, los separamos y agrupamos por pymes
        //Map ===>>> [clave] = pyme ; [valor] = lista de productos
        Map<PymeEntity, List<OrderProduct>> productsByPyme;
        try {
            productsByPyme = groupProductsByPyme(command.products());
        } catch (NoSuchElementException e) {
            return new Result.InvalidFields("Problemas con Lista de Productos",new String[]{"products"});
        }

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

        Result productValidation = validateProducts(productsByPyme);
        if (productValidation != null) return productValidation;

        Result stockValidation = validateStock(productsByPyme);
        if (stockValidation != null) return stockValidation;

        UUID orderId = UUID.randomUUID();
        return new Result.Success(orderId);
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
            return new Result.InvalidFields("El método de pago no es válido o no está activo","shippingMethod");
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

                if (!productEntity.getIsActive() || !productEntity.getAvailable()) {
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


    //Considerar si manejar ERROR_CODE para los Result
    //¿Un método para verificar pymes, usuarios??

    //Falta la logica de Crear los Orders y OrderLines (siguiente spring)

    private boolean validatePyme(List<OrderProduct> products) {
        if (products == null || products.isEmpty()) return false;

        for (OrderProduct p : products) {
            if (p.productId() == null || p.quantity() <= 0) return false;

            // Validar que el producto exista y esté activo/disponible
            Optional<ProductEntity> product = productQuery.findById(p.productId());
            if (product.isEmpty()) return false;

            ProductEntity pEntity = product.get();
            if (!pEntity.getIsActive() || !pEntity.getAvailable()) return false;
        }

        return true;
    }


}

