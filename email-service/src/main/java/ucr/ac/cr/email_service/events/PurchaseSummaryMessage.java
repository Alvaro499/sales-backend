package ucr.ac.cr.email_service.events;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseSummaryMessage(
        String customerEmail,
        String customerFirstName,
        String customerLastName,
        String phone,
        String shippingAddress,
        String shippingMethod,
        String  paymentMethod,
        BigDecimal grandTotal,
        List<PymeOrder> orders
) {

    public record PymeOrder(
            String pymeEmail,
            String pymeName,
            BigDecimal total,
            List<Product> products
    ) {}

    public record Product(
            String name,
            int quantity,
            BigDecimal  unitPrice,
            BigDecimal  priceWithDiscount,
            BigDecimal  promotionApplied,
            BigDecimal  subtotal
    ) {}
}