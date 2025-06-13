package ucr.ac.cr.email_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.models.PurchaseSummaryMessage;
import ucr.ac.cr.email_service.service.EmailService;
import ucr.ac.cr.email_service.templates.EmailTemplate;

import java.util.Map;

@Component
public class PurchaseSummaryListener {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseSummaryListener.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "purchase-summary", groupId = "mail-service")
    public void consume(String jsonPayload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PurchaseSummaryMessage msg = mapper.readValue(jsonPayload, PurchaseSummaryMessage.class);

            sendEmailToCustomer(msg);
            sendEmailsToPymes(msg);

        } catch (Exception e) {
            logger.error("Error al deserializar en Recuperar Contraseña: {}", e.getMessage(), e);

        }
    }

    private void sendEmailToCustomer(PurchaseSummaryMessage msg){
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
                "order_summary_to_customer",
                variables,
                null,
                null
        );

        emailService.sendHtmlEmail(template);
    }

    private void sendEmailsToPymes(PurchaseSummaryMessage msg) {
        for (PurchaseSummaryMessage.PymeOrder order : msg.orders()) {
            Map<String, Object> variables = Map.of(
                    "pymeName", order.pymeName(),
                    "firstName", msg.customerFirstName(),
                    "lastName", msg.customerLastName(),
                    "email", msg.customerEmail(),
                    "phone", msg.phone(),
                    "shippingAddress", msg.shippingAddress(),
                    "products", order.products(),
                    "orderTotal", order.total()
            );

            EmailTemplate template = new EmailTemplate(
                    order.pymeEmail(),
                    "Nuevo pedido recibido",
                    "order_notification_to_pyme",
                    variables,
                    null,
                    null
            );

            emailService.sendHtmlEmail(template);
        }
    }
}
