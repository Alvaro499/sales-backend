package ucr.ac.cr.email_service.events;

import java.util.List;

public record PurchaseSummaryMessage(
        String customerEmail,
        String customerFirstName,
        String customerLastName,
        String phone,
        String shippingAddress,
        String shippingMethod,
        String paymentMethod,
        String grandTotal,
        List<PymeOrder> orders
) {

    public record PymeOrder(
            String pymeEmail,
            String pymeName,
            String total,
            List<Product> products
    ) {}

    public record Product(
            String name,
            int quantity,
            String unitPrice,
            String subtotal
    ) {}
}

