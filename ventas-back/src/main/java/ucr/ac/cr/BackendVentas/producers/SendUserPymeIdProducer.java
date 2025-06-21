package ucr.ac.cr.BackendVentas.producers;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.events.SendUserPymeIdEvent;

@Component
public class SendUserPymeIdProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "register-user";

    @Autowired
    public SendUserPymeIdProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public boolean sendUserPymeId(SendUserPymeIdEvent event) {
        try {
            String message = "PymeId: " + event.getUserRegistrationDTO().pymeId() + " | UserId: " + event.getUserRegistrationDTO().userId();
            kafkaTemplate.send(TOPIC, message);  // Enviamos el mensaje en el topic "register-user"
            return true;
        } catch (Exception e) {
            return false;  // Si hubo un error, devolvemos false
        }
    }
}
