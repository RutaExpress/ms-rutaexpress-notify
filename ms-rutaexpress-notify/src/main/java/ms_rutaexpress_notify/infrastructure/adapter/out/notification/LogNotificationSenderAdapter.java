package ms_rutaexpress_notify.infrastructure.adapter.out.notification;

import lombok.extern.slf4j.Slf4j;
import ms_rutaexpress_notify.domain.exception.TemporaryFailureException;
import ms_rutaexpress_notify.domain.ports.out.NotificationSenderPort;
import org.springframework.stereotype.Component;

// Adaptador de salida: simula el envío del email en el log
@Slf4j
@Component
public class LogNotificationSenderAdapter implements NotificationSenderPort {

    private final SmtpFailureSimulator smtpFailureSimulator;

    public LogNotificationSenderAdapter(SmtpFailureSimulator smtpFailureSimulator) {
        this.smtpFailureSimulator = smtpFailureSimulator;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        if (smtpFailureSimulator.shouldFail()) {
            // Falla temporal: el servidor de correo no responde -> se debe reintentar
            throw new TemporaryFailureException("Servidor de correo no disponible (simulado)");
        }
        log.info("[EMAIL] para={} | asunto={} | cuerpo={}", to, subject, body);
    }
}