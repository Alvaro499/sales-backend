package ucr.ac.cr.email_service.events;

public record PymeRegisteredEvent(String email, String code, String pymeName) {}
