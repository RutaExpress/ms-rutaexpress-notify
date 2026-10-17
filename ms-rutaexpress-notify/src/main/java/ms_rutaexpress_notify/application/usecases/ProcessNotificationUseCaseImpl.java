package ms_rutaexpress_notify.application.usecases;

import ms_rutaexpress_notify.domain.model.ShipmentEvent;
import ms_rutaexpress_notify.domain.ports.in.ProcessNotificationUseCase;
import ms_rutaexpress_notify.domain.ports.out.NotificationSenderPort;
import org.springframework.stereotype.Service;
@Service  // registra el caso de uso para que el listener lo pueda usar
public class ProcessNotificationUseCaseImpl implements ProcessNotificationUseCase {

    private final NotificationSenderPort notificationSenderPort;

    public ProcessNotificationUseCaseImpl(NotificationSenderPort notificationSenderPort) {
        this.notificationSenderPort = notificationSenderPort;
    }

    @Override
    public void process(ShipmentEvent event) {
        // Regla: sin evento o sin email del destinatario no se puede notificar
        if (event == null || event.customerEmail() == null) {
            throw new IllegalArgumentException("El evento o el correo del cliente no pueden ser nulos");
        }

        String subject = "Actualización de su envío: " + event.shipmentId();
        String body = String.format("Hola, el estado de su envío (%s) ha cambiado a: %s. %s",
                event.shipmentId(),
                event.status(),
                event.message() != null ? event.message() : "");

        notificationSenderPort.sendEmail(event.customerEmail(), subject, body);
    }

    
}