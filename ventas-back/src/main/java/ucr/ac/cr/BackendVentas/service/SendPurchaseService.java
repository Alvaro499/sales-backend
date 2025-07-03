package ucr.ac.cr.BackendVentas.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.events.ProductSendDTO;

@Service
public class SendPurchaseService {

    private final KafkaTemplate<String, ProductSendDTO> kafkaTemplate;

    private static final String TOPIC = "order_topic4";

    public SendPurchaseService(KafkaTemplate<String, ProductSendDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(ProductSendDTO message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(message);
            System.out.println("🔍 Enviando JSON: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(TOPIC, message);
    }
}