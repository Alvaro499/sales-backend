package ucr.ac.cr.BackendVentas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private static final String TOPIC = "user-registered2";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEmailEvent(String email) {
        kafkaTemplate.send("user-registered2", email);
    }

}
