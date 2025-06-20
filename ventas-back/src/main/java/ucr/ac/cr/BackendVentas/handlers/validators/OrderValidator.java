package ucr.ac.cr.BackendVentas.handlers.validators;

import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.handlers.queries.PaymentMethodQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.PymeQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ProductQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ShippingMethodQuery;
import ucr.ac.cr.BackendVentas.models.OrderProduct;
import ucr.ac.cr.BackendVentas.models.ErrorCode;
import ucr.ac.cr.BackendVentas.jpa.entities.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ucr.ac.cr.BackendVentas.utils.ValidationUtils.validationError;

@Component
public class OrderValidator {

    private final ProductQuery productQuery;
    private final PymeQuery pymeQuery;
    private final PaymentMethodQuery paymentMethodQuery;
    private final ShippingMethodQuery shippingMethodQuery;
    private final OrderLineValidator orderLineValidator;

    public OrderValidator(ProductQuery productQuery, PymeQuery pymeQuery,
                          PaymentMethodQuery paymentMethodQuery, ShippingMethodQuery shippingMethodQuery, OrderLineValidator orderLineValidator) {
        this.productQuery = productQuery;
        this.pymeQuery = pymeQuery;
        this.paymentMethodQuery = paymentMethodQuery;
        this.shippingMethodQuery = shippingMethodQuery;
        this.orderLineValidator = orderLineValidator;
    }



    public void validatePaymentMethod(String method) {
        if (method == null || method.isBlank()) {
            throw validationError("El método de pago es obligatorio", ErrorCode.REQUIRED_FIELDS, "paymentMethod");
        }
        boolean exists = paymentMethodQuery.existsByNameIgnoreCaseAndIsActiveTrue(method);
        if (!exists) {
            throw validationError("Método de pago inválido o inactivo", ErrorCode.INVALID_FORMAT, "paymentMethod");
        }
    }

    public void validateShippingMethod(String method) {
        if (method == null || method.isBlank()) {
            throw validationError("El método de envío es obligatorio", ErrorCode.REQUIRED_FIELDS, "shippingMethod");
        }
        boolean exists = shippingMethodQuery.existsByNameIgnoreCaseAndIsActiveTrue(method);
        if (!exists) {
            throw validationError("Método de envío inválido o inactivo", ErrorCode.INVALID_FORMAT, "shippingMethod");
        }
    }

    public void validatePymes(Set<PymeEntity> pymes) {
        for (PymeEntity pyme : pymes) {
            if (pyme == null) {
                throw validationError("Una de las pymes asociadas es nula", ErrorCode.REQUIRED_FIELDS, "pymes");
            }
            Optional<PymeEntity> foundPyme = pymeQuery.findById(pyme.getId());
            if (foundPyme.isEmpty()) {
                throw validationError("La pyme con ID " + pyme.getId() + " no existe", ErrorCode.ENTITY_NOT_FOUND, "pymes");
            }
            if (!foundPyme.get().getActive()) {
                throw validationError("La pyme " + foundPyme.get().getName() + " está inactiva", ErrorCode.INVALID_FORMAT, "pymes");
            }
        }
    }

    public void validateProducts(Map<PymeEntity, List<OrderProduct>> productsByPyme) {
        for (List<OrderProduct> productList : productsByPyme.values()) {
            for (OrderProduct orderedProduct : productList) {

                if (orderedProduct.productId() == null) {
                    throw validationError("El ID del producto es obligatorio", ErrorCode.REQUIRED_FIELDS, "products");
                }
                if (orderedProduct.quantity() <= 0) {
                    throw validationError("La cantidad debe ser mayor que cero", ErrorCode.INVALID_FORMAT, "products");
                }
                Optional<ProductEntity> foundProduct = productQuery.findById(orderedProduct.productId());
                if (foundProduct.isEmpty()) {
                    throw validationError("Producto no encontrado: " + orderedProduct.productId(), ErrorCode.ENTITY_NOT_FOUND, "products");
                }
                ProductEntity product = foundProduct.get();

                orderLineValidator.validatePromotion(product);

                if (!product.isActive() || !product.getAvailable()) {
                    throw validationError("El producto '" + product.getName() + "' no está disponible", ErrorCode.INVALID_FORMAT, "products");
                }
            }
        }
    }

    public void validateStock(Map<PymeEntity, List<OrderProduct>> productsByPyme) {

        for (List<OrderProduct> productList : productsByPyme.values()) {

            for (OrderProduct orderedProduct : productList) {

                Optional<ProductEntity> foundProduct = productQuery.findById(orderedProduct.productId());
                if (foundProduct.isEmpty()) {
                    throw validationError("Producto no encontrado: " + orderedProduct.productId(), ErrorCode.ENTITY_NOT_FOUND, "products");
                }
                ProductEntity product = foundProduct.get();
                if (orderedProduct.quantity() > product.getStock()) {
                    throw validationError(
                            "El producto '" + product.getName() + "' no tiene suficiente stock. Pedido: " +
                                    orderedProduct.quantity() + ", Disponible: " + product.getStock(),
                            ErrorCode.OUT_OF_STOCK,
                            "products"
                    );
                }
            }
        }
    }
}
