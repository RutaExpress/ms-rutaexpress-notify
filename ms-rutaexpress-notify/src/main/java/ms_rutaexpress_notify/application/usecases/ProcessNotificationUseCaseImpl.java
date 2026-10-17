package ms_rutaexpress_notify.application.usecases;

import ms_rutaexpress_notify.domain.exception.InvalidMessageException;
import ms_rutaexpress_notify.domain.model.ShipmentEvent;
import ms_rutaexpress_notify.domain.ports.in.ProcessNotificationUseCase;
import ms_rutaexpress_notify.domain.ports.out.NotificationSenderPort;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

// Caso de uso: valida el mensaje y envía el email al destinatario
@Service
public class ProcessNotificationUseCaseImpl implements ProcessNotificationUseCase {

    // Formato básico de email: algo@algo.algo
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final NotificationSenderPort notificationSenderPort;

    public ProcessNotificationUseCaseImpl(NotificationSenderPort notificationSenderPort) {
        this.notificationSenderPort = notificationSenderPort;
    }

    @Override
    public void process(ShipmentEvent event) {
        validate(event);  // si algo falta, lanza InvalidMessageException

        String subject = "Actualización de su envío: " + event.shipmentId();
        String body = String.format("Hola, el estado de su envío (%s) ha cambiado a: %s. %s",
                event.shipmentId(),
                event.status(),
                event.message() != null ? event.message() : "");

        notificationSenderPort.sendEmail(event.customerEmail(), subject, body);
    }

    // Reglas del mensaje: errores NO recuperables
    private void validate(ShipmentEvent event) {
        if (event == null) {
            throw new InvalidMessageException("Mensaje vacío");
        }
        if (event.shipmentId() == null || event.shipmentId().isBlank()) {
            throw new InvalidMessageException("Falta shipmentId");
        }
        if (event.customerEmail() == null || !EMAIL_PATTERN.matcher(event.customerEmail()).matches()) {
            throw new InvalidMessageException("Email del destinatario inválido: " + event.customerEmail());
        }
    }
}