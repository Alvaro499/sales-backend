package ucr.ac.cr.BackendVentas.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MonetaryUtilsTest {

    @Test
    void testRound_HappyPath() {
        BigDecimal input = new BigDecimal("15.6789");
        BigDecimal expected = new BigDecimal("15.68"); // redondeo HALF_UP
        assertEquals(expected, MonetaryUtils.round(input));
    }

    @Test
    void testRound_NullValue() {
        assertEquals(BigDecimal.ZERO, MonetaryUtils.round(null));
    }

    @Test
    void testApplyPromotion_90PercentDiscount() {
        BigDecimal price = new BigDecimal("100.00");
        BigDecimal promotion = new BigDecimal("0.90"); // 90% de descuento

        BigDecimal expected = new BigDecimal("10.00");
        assertEquals(expected, MonetaryUtils.applyPromotion(price, promotion));
    }

    @Test
    void testApplyPromotion_ZeroDiscount() {
        BigDecimal price = new BigDecimal("75.00");
        BigDecimal promotion = BigDecimal.ZERO;

        BigDecimal expected = new BigDecimal("75.00");
        assertEquals(expected, MonetaryUtils.applyPromotion(price, promotion));
    }

    @Test
    void testApplyPromotion_NullPromotion() {
        BigDecimal price = new BigDecimal("50.00");
        BigDecimal promotion = null;  // sin descuento

        BigDecimal expected = new BigDecimal("50.00");
        assertEquals(expected, MonetaryUtils.applyPromotion(price, promotion));
    }
}
