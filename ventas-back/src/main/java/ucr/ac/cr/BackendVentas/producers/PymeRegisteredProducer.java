package ucr.ac.cr.BackendVentas.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.events.PymeRegisteredEvent;

@Service
public class PymeRegisteredProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public PymeRegisteredProducer(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean sendEmailConfirmation(PymeRegisteredEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("pyme-confirmation-code", json);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar evento a Kafka: " + e.getMessage());
            return false;
        }
    }
}