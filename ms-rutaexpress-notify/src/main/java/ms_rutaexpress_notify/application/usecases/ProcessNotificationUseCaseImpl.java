package ms_rutaexpress_notify.application.usecases;

import ms_rutaexpress_notify.domain.model.ShipmentEvent;
import ms_rutaexpress_notify.domain.ports.in.ProcessNotificationUseCase;
import ms_rutaexpress_notify.domain.ports.out.NotificationSenderPort;

public class ProcessNotificationUseCaseImpl implements ProcessNotificationUseCase {

    private final NotificationSenderPort notificationSenderPort;

    public ProcessNotificationUseCaseImpl(NotificationSenderPort notificationSenderPort) {
        this.notificationSenderPort = notificationSenderPort;
    }

    @Override
    public void process(ShipmentEvent event) {
        if (event == null || event.getCustomerEmail() == null) {
            throw new IllegalArgumentException("El evento o el correo del cliente no pueden ser nulos");
        }

        String subject = "Actualización de su envío: " + event.getShipmentId();
        String body = String.format("Hola, el estado de su envío (%s) ha cambiado a: %s. %s",
                event.getShipmentId(),
                event.getStatus(),
                event.getMessage() != null ? event.getMessage() : "");

        notificationSenderPort.sendEmail(event.getCustomerEmail(), subject, body);
    }
}