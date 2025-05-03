package ucr.ac.cr.BackendVentas.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendValidationEmail(String email) {
        // TODO: Conectar con el servicio de envío de correos
        System.out.println("Sending validation email to: " + email);
    }
}
