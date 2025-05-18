package ucr.ac.cr.BackendVentas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final String TOPIC = "user-registered";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEmailEvent(String email) {
        kafkaTemplate.send(TOPIC, email);
        System.out.println("Sent email event for: " + email);
    }

    public void sendValidationEmail(String email) {
        // TODO: Conectar con el servicio de envío de correos
        System.out.println("Sending validation email to: " + email);
    }
}
