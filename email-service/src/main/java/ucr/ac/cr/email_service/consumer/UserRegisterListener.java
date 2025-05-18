package ucr.ac.cr.email_service.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.service.EmailService;

@Component
public class UserRegisterListener {
    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "user-registered", groupId = "mail-service")
    public void consume(String email) {
        emailService.sendSimpleEmail(email, "Bienvenido a la plataforma", "Gracias por registrarte.");
    }
}
