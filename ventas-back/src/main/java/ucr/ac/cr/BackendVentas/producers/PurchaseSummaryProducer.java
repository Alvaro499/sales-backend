package ucr.ac.cr.BackendVentas.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ucr.ac.cr.BackendVentas.events.PurchaseSummaryMessage;

@Component
public class PurchaseSummaryProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PurchaseSummaryProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean sendEmailSummary(PurchaseSummaryMessage event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("purchase-summary", payload);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar resumen de compra a Kafka: " + e.getMessage());
            return false;
        }
    }
}