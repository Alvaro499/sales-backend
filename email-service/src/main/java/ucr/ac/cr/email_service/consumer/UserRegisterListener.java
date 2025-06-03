package ucr.ac.cr.email_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.models.PasswordRecoveryMessage;
import ucr.ac.cr.email_service.service.EmailService;

@Component
public class UserRegisterListener {
    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "user-registered2", groupId = "mail-service")
    public void consume(String email) {
        emailService.sendSimpleEmail(email, "Bienvenido a la plataforma", "Gracias por registrarte.");
    }

    @KafkaListener(topics = "password-recovery", groupId = "mail-service")
    public void consumePasswordRecovery(String jsonPayload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            PasswordRecoveryMessage msg = mapper.readValue(jsonPayload, PasswordRecoveryMessage.class);

            String subject = "Recuperación de contraseña";
            String url = "http://localhost:5173/reset-password/" + msg.token();
            String text = "Haz clic en el siguiente enlace para recuperar tu contraseña:\n" + url;
            emailService.sendSimpleEmail(msg.email(), subject, text);

        } catch (Exception e) {
            System.err.println("Error procesando mensaje Kafka: " + e.getMessage());
        }
    }
}
