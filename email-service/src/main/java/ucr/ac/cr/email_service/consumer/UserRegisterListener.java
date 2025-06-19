package ucr.ac.cr.email_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.events.PasswordRecoveryMessage;
import ucr.ac.cr.email_service.service.EmailService;
import ucr.ac.cr.email_service.templates.EmailTemplate;
import ucr.ac.cr.email_service.templates.EmailTemplateName;

import java.util.Map;

@Component
public class UserRegisterListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegisterListener.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "user-registered2", groupId = "mail-service")
    public void consume(String email) {
        emailService.sendSimpleEmail(email, "Bienvenido a la plataforma", "Gracias por registrarte.");
    }

    @KafkaListener(topics = "password-recovery", groupId = "mail-service")
    public void consumePasswordRecovery(String jsonPayload) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            PasswordRecoveryMessage msg = mapper.readValue(jsonPayload, PasswordRecoveryMessage.class);

            Map<String, Object> emailVariables = Map.of(
                    "email", msg.email(),
                    "url", "http://localhost:5173/reset-password/" + msg.token()
            );

            EmailTemplate emailTemplate = new EmailTemplate(
                    msg.email(),
                    "Recuperación de contraseña",
                    EmailTemplateName.PASSWORD_RECOVERY,
                    emailVariables,
                    null,
                    null
            );

            emailService.sendHtmlEmail(emailTemplate);
        } catch (Exception e) {
            logger.error("Error al enviar correo de recuperación de contraseña: {}", e.getMessage(), e);
        }
    }
}
