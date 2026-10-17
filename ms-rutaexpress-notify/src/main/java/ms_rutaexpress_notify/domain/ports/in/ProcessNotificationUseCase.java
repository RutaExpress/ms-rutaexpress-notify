package ms_rutaexpress_notify.domain.ports.in;

import ms_rutaexpress_notify.domain.model.ShipmentEvent;

public interface ProcessNotificationUseCase {
    void process(ShipmentEvent event);
}