package ucr.ac.cr.email_service.templates;

import java.util.Map;

/** POJO class para representar una plantilla de correo electrónico.
 * Esta clase encapsula los detalles necesarios para enviar un correo,
 * incluyendo destinatario, asunto, nombre de la plantilla, variables para la plantilla
 * y detalles opcionales de imagen.
 */
public class EmailTemplate {
    private final String to;
    private final String subject;
    private final EmailTemplateName templateName;
    private final Map<String, Object> variables;
    private final String imageId;
    private final String imagePath;

    public EmailTemplate(String to, String subject, EmailTemplateName templateName,
                         Map<String, Object> variables, String imageId, String imagePath) {
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
        this.variables = variables;
        this.imageId = imageId;
        this.imagePath = imagePath;
    }

    public String getTo() { return to; }
    public String getSubject() { return subject; }
    public EmailTemplateName getTemplateName() { return templateName; }
    public Map<String, Object> getVariables() { return variables; }
    public String getImageId() { return imageId; }
    public String getImagePath() { return imagePath; }
}
