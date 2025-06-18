package ucr.ac.cr.email_service.models;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * Clase plantilla para atratpar fallos en el envío de correos electrónicos.
 * Esta clase captura los detalles de un correo que no se pudo enviar,
 * incluyendo el destinatario, asunto, nombre de la plantilla, variables utilizadas
 * y el mensaje de error; para luego escribirlo en el LogFile de SpringBoot.
 */

public class EmailErrorLog {
    private final LocalDateTime timestamp;
    private final String recipient;
    private final String subject;
    private final String templateName;
    private final Map<String, Object> variables;
    private final String message;

    public EmailErrorLog(String recipient,
                         String subject,
                         String templateName,
                         Map<String, Object> variables,
                         String message) {
        this.timestamp = LocalDateTime.now();
        this.recipient = recipient;
        this.subject = subject;
        this.templateName = templateName;
        this.variables = variables;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[EMAIL ERROR] " + timestamp +
                " | To: " + recipient +
                " | Subject: " + subject +
                " | Template: " + templateName +
                " | Variables: " + variables +
                " | Error: " + message;
    }
}