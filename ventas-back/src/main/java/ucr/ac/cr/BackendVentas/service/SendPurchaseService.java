package ucr.ac.cr.BackendVentas.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ucr.ac.cr.BackendVentas.events.ProductSendDTO;

@Service
public class SendPurchaseService {

    private final KafkaTemplate<String, ProductSendDTO> kafkaTemplate;

    private static final String TOPIC = "order_topic";

    public SendPurchaseService(KafkaTemplate<String, ProductSendDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(ProductSendDTO message) {
        kafkaTemplate.send(TOPIC, message);
    }
}