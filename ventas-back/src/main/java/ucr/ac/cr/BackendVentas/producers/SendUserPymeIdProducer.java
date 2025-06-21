package ucr.ac.cr.BackendVentas.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.events.SendUserPymeIdEvent;

@Component
public class SendUserPymeIdProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "register-user1";

    @Autowired
    public SendUserPymeIdProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public boolean sendUserPymeId(SendUserPymeIdEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event.getUserRegistrationDTO());
            kafkaTemplate.send(TOPIC, json);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje Kafka: " + e.getMessage());
            return false;
        }
    }
}
