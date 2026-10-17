package ms_rutaexpress_notify.infrastructure.adapter.out.notification;

import lombok.extern.slf4j.Slf4j;
import ms_rutaexpress_notify.domain.ports.out.NotificationSenderPort;
import org.springframework.stereotype.Component;

// Implementa el puerto de salida: simula el envío del email escribiéndolo en el log
@Slf4j
@Component
public class LogNotificationSenderAdapter implements NotificationSenderPort {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("[EMAIL] para={} | asunto={} | cuerpo={}", to, subject, body);
    }
}