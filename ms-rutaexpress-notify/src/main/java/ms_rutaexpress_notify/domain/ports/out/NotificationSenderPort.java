package ms_rutaexpress_notify.domain.ports.out;

public interface NotificationSenderPort {
    void sendEmail(String to, String subject, String body);
}