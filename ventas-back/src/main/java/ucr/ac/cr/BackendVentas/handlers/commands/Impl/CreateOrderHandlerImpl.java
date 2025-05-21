package ucr.ac.cr.BackendVentas.handlers.commands.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.handlers.commands.CreateOrderHandler;
import ucr.ac.cr.BackendVentas.handlers.queries.PaymentMethodQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ProductQuery;
import ucr.ac.cr.BackendVentas.handlers.queries.ShippingMethodQuery;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
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

        UUID userId = null;
        boolean isValidEmail = validateEmail(command.email());
        boolean isValidFirstName = validateName(command.firstName());
        boolean isValidLastName = validateName(command.lastName());
        boolean isValidPhone = validatePhone(command.phone());
        boolean isValidShippingAddress = validateShippingAddress(command.shippingAddress());
        boolean isValidPaymentMethod = validatePaymentMethod(command.paymentMethod());
        boolean isValidShippingMethod = validateShippingMethod(command.shippingMethod());
        boolean isValidProducts = validateProducts(command.products());
        boolean isStockAvaliable = validateStock(command.products());


        // Validación general de campos
        boolean isValidFields = isValidEmail
                                && isValidFirstName
                                && isValidLastName
                                && isValidPhone
                                && isValidShippingAddress
                                && isValidPaymentMethod
                                && isValidShippingMethod
                                && isValidProducts
                                && isStockAvaliable;

        if (!isValidFields) {

            // Por simplicidad solo se envia un mensaje con los campos inválidos detectados
            List<String> invalidFields = new java.util.ArrayList<>();
            if (!isValidEmail) invalidFields.add("email");
            if (!isValidFirstName) invalidFields.add("firstName");
            if (!isValidLastName) invalidFields.add("lastName");
            if (!isValidPhone) invalidFields.add("phone");
            if (!isValidShippingAddress) invalidFields.add("shippingAddress");
            if (!isValidPaymentMethod) invalidFields.add("paymentMethod");
            if (!isValidShippingMethod) invalidFields.add("shippingMethod");
            if (!isValidProducts) invalidFields.add("products");

            return new Result.InvalidFields(invalidFields.toArray(new String[0]));
        }
        return new Result.Success(userId);

    }

    // Métodos privados para validaciones

    private boolean validateEmail(String email) {
        if (email == null || email.isBlank()) return false;
        // Validación simple de email con regex
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean validateName(String name) {

        return name != null && !name.isBlank() && name.length() <= 100;
    }

    private boolean validatePhone(String phone) {
        if (phone == null || phone.isBlank()) return false;
        // Validar solo números
        //return phone.matches("\\d{7,15}");
        return phone.matches("[\\d-]{7,15}"); //con guion

    }

    private boolean validateShippingAddress(String address) {
        return address != null && !address.isBlank() && address.length() <= 200;
    }

    private boolean validateProducts(List<OrderProduct> products) {
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


    public boolean validateStock(List<OrderProduct> products) {
        for (OrderProduct product : products) {
            int availableStock = productQuery.getAvailableStock(product.productId());
            boolean isStockSufficient = product.quantity() > 0 && product.quantity() <= availableStock;

            if (!isStockSufficient) {
                return false;
            }
        }
        return true;
    }

    private boolean validatePaymentMethod(String method) {
        boolean isNotBlank = method != null && !method.isBlank();
        boolean existsAndActive = isNotBlank && paymentMethodQuery.existsByNameIgnoreCaseAndIsActiveTrue(method);
        return existsAndActive;
    }

    private boolean validateShippingMethod(String method) {
        boolean isNotBlank = method != null && !method.isBlank();
        boolean existsAndActive = isNotBlank && shippingMethodQuery.existsByNameIgnoreCaseAndIsActiveTrue(method);
        return existsAndActive;
    }


    //¿Un método para manejar PYMES??

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

    /*
    private boolean checkUserExists(UUID userId) {
        return userId != null;
    }
    */

    /*
    private UUID createOrder(Command command) {
        return UUID.randomUUID();
    }
    */

}

