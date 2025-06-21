package ucr.ac.cr.email_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/* Clase necesaria para que Jackson pueda serializar y deserializar correctamente los objetos
 * que contienen tipos de datos como Optional, LocalDateTime y otros Record subanidados.
 *
 * https://stackoverflow.com/questions/68394911/why-record-class-cant-be-properly-deserialized-with-jackson
 *
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }
}
