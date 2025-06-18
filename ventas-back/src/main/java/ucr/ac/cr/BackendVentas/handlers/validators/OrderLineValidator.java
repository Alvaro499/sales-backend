package ucr.ac.cr.BackendVentas.handlers.validators;

import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.jpa.entities.ProductEntity;
import ucr.ac.cr.BackendVentas.models.ErrorCode;

import java.math.BigDecimal;

import static ucr.ac.cr.BackendVentas.utils.ValidationUtils.validationError;

@Component
public class OrderLineValidator {

    public void validatePromotion(ProductEntity product) {
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
    }
}

