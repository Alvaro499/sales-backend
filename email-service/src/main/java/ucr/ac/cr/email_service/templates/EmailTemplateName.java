package ucr.ac.cr.email_service.templates;

public enum EmailTemplateName {
    ACTIVATION_CODE_TO_PYME("activation_code_to_pyme"),
    ORDER_NOTIFICATION_TO_PYME("order_notification_to_pyme"),
    ORDER_SUMMARY_TO_CUSTOMER("order_summary_to_customer"),
    PASSWORD_RECOVERY("password_recovery");

    private final String templateFilename;

    EmailTemplateName(String filename) {
        this.templateFilename = filename;
    }

    public String getFilename() {
        return templateFilename;
    }
}

