package ucr.ac.cr.email_service.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ucr.ac.cr.email_service.models.EmailErrorLog;
import ucr.ac.cr.email_service.templates.EmailTemplate;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /*
     * Integra el motor de plantillas Thymeleaf con el framework Spring. Procesa plantillas Thymeleaf
    */
    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendHtmlEmail(EmailTemplate emailTemplate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(emailTemplate.getTo());
            helper.setSubject(emailTemplate.getSubject());
            helper.setFrom("ventas.pymes25@gmail.com");

            Context context = new Context();
            context.setVariables(emailTemplate.getVariables());

            String html = templateEngine.process(emailTemplate.getTemplateName().getFilename(), context);

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            EmailErrorLog errorLog = new EmailErrorLog(
                    emailTemplate.getTo(),
                    emailTemplate.getSubject(),
                    emailTemplate.getTemplateName().getFilename(),
                    emailTemplate.getVariables(),
                    e.getMessage()
            );
            logger.error(errorLog.toString(), e.getMessage());
        }
    }
}
