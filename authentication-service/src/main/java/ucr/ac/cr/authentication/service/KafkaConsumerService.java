package ucr.ac.cr.authentication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ucr.ac.cr.authentication.api.types.RegisterUserPymeDTO;
import ucr.ac.cr.authentication.jpa.entities.UserPymeEntity;
import ucr.ac.cr.authentication.jpa.repositories.UserPymeRepository;

@Service
public class KafkaConsumerService {

    @Autowired
    private UserPymeRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "register-user1", groupId = "auth-service")
    public void listen(RegisterUserPymeDTO dto) {
        try {
            UserPymeEntity entity = new UserPymeEntity();
            entity.setUserId(dto.userId());
            entity.setPymeId(dto.pymeId());

            repository.save(entity);

        } catch (Exception e) {
            System.err.println("Error al procesar mensaje Kafka: " + e.getMessage());
        }
    }
}
