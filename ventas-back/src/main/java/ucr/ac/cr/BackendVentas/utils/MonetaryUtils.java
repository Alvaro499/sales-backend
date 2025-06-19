package ucr.ac.cr.BackendVentas.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonetaryUtils {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal round(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * La promoción se maneja entre valores de 0 y 1, donde 0 es sin descuento y 0.9
     * es un descuento del 90%.
     * La fórmula es: finalPrice = price × (1 - promotion)
     * Si la promoción es 0.20 → discountFactor = 1 - 0.20 = 0.80
     */
    public static BigDecimal applyPromotion(BigDecimal price, BigDecimal promotion) {
        if (promotion == null) promotion = BigDecimal.ZERO;

        BigDecimal discountFactor = BigDecimal.ONE.subtract(promotion);
        return round(price.multiply(discountFactor));
    }
}