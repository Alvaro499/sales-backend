package ucr.ac.cr.email_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.models.PurchaseSummaryMessage;
import ucr.ac.cr.email_service.service.EmailService;
import ucr.ac.cr.email_service.templates.EmailTemplate;

import java.util.List;
import java.util.Map;

@Component
public class EmailTestRunner implements CommandLineRunner {

    @Autowired
    private EmailService emailService;

    @Override
    public void run(String... args) {
        // 1. Crear productos
        List<PurchaseSummaryMessage.Product> products = List.of(
                new PurchaseSummaryMessage.Product("Café Premium", 2, "3500", "7000"),
                new PurchaseSummaryMessage.Product("Pan Casero", 1, "1500", "1500")
        );

        // 2. Crear orden de pyme
        List<PurchaseSummaryMessage.PymeOrder> orders = List.of(
                new PurchaseSummaryMessage.PymeOrder("aldasi2000@hotmail.com", "Delicias Ticas", "8500", products)
        );

        // 3. Crear mensaje de resumen de compra
        PurchaseSummaryMessage msg = new PurchaseSummaryMessage(
                "alvarosiles499@gmail.com",
                "María",
                "Rodríguez",
                "8888-8888",
                "San José, Costa Rica",
                "Correo Rápido",
                "Sinpe Móvil",
                "8500",
                orders
        );

        // 4. Crear y enviar el correo
        Map<String, Object> variables = Map.of(
                "firstName", msg.customerFirstName(),
                "lastName", msg.customerLastName(),
                "email", msg.customerEmail(),
                "phone", msg.phone(),
                "shippingAddress", msg.shippingAddress(),
                "shippingMethod", msg.shippingMethod(),
                "paymentMethod", msg.paymentMethod(),
                "orders", msg.orders(),
                "grandTotal", msg.grandTotal()
        );

        EmailTemplate template = new EmailTemplate(
                msg.customerEmail(),
                "Resumen de tu compra",
                "aaa",
                variables,
                null,
                null
        );

        emailService.sendHtmlEmail(template);
    }
}
