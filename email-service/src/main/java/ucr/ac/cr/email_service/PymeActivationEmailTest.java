package ucr.ac.cr.email_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ucr.ac.cr.email_service.consumer.PymeConfirmationCodeListener;
import ucr.ac.cr.email_service.events.PymeRegisteredEvent;


/* * Este componente se utiliza para probar el envío de correos electrónicos de activación de pymes.
 *
 * Correo: ventas.pymes5.pruebas@gmail.com
 * Contraseña: 12345678Q#
 *
 *  */

@Component
public class PymeActivationEmailTest implements CommandLineRunner {

    @Autowired
    private PymeConfirmationCodeListener listener;

    @Override
    public void run(String... args) {
        try {
            // Crear un evento de prueba
            PymeRegisteredEvent event = new PymeRegisteredEvent(
                    "ventas.pymes5.pruebas@gmail.com",
                    "1234",
                    "Mi Pyme de Prueba"
            );

            // Convertir a JSON
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(event);

            // Simular el consumo del mensaje
            listener.consume(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
