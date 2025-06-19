package ucr.ac.cr.BackendVentas.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonetaryUtils {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Redondea un valor monetario a 2 decimales usando HALF_UP.
     *
     * @param value El valor a redondear.
     * @return BigDecimal redondeado a 2 decimales.
     */
    public static BigDecimal round(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(SCALE, ROUNDING_MODE);
    }
}