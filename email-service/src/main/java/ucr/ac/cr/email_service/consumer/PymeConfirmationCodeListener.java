package ucr.ac.cr.email_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.events.PymeRegisteredEvent;
import ucr.ac.cr.email_service.service.EmailService;
import ucr.ac.cr.email_service.templates.EmailTemplate;
import ucr.ac.cr.email_service.templates.EmailTemplateName;

import java.util.Map;

@Component
public class PymeConfirmationCodeListener {

    private static final Logger logger = LoggerFactory.getLogger(PymeConfirmationCodeListener.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "pyme-confirmation-code", groupId = "mail-service")
    public void consume(String jsonPayload) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            PymeRegisteredEvent msg = mapper.readValue(jsonPayload, PymeRegisteredEvent.class);

            Map<String, Object> emailVariables = Map.of(
                    "email", msg.email(),
                    "confirmationCode", msg.code(),
                    "pymeName", msg.pymeName()
            );

            EmailTemplate emailTemplate = new EmailTemplate(
                    msg.email(),
                    "Código de activación",
                    EmailTemplateName.ACTIVATION_CODE_TO_PYME,
                    emailVariables,
                    null,
                    null
            );

            emailService.sendHtmlEmail(emailTemplate);

            logger.info("Recibiendo mensaje de código de confirmación de Pyme: {}", jsonPayload);
        } catch (Exception e) {
            logger.error("Error al recibir mensaje de código de confirmación de Pyme: {}", e.getMessage(), e);
        }
    }
}
