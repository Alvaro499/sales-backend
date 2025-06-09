package ucr.ac.cr.email_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.models.ConfirmationCodeMessage;
import ucr.ac.cr.email_service.service.EmailService;

@Component
public class ConfirmationCodeListener {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "confirmation-code", groupId = "mail-service")
    public void consumeConfirmationCode(String jsonPayload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConfirmationCodeMessage msg = mapper.readValue(jsonPayload, ConfirmationCodeMessage.class);

            String subject = "Código de confirmación de Pyme";
            String text = String.format("Tu código de verificación es: %s\nEste código expirará en 15 minutos.", msg.code());
            emailService.sendSimpleEmail(msg.email(), subject, text);

        } catch (Exception e) {
            System.err.println("Error procesando mensaje de código de confirmación: " + e.getMessage());
        }
    }
}
