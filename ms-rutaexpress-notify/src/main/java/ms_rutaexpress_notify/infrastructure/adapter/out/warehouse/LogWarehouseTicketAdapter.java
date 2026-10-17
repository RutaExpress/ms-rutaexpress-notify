package ms_rutaexpress_notify.infrastructure.adapter.out.warehouse;

import lombok.extern.slf4j.Slf4j;
import ms_rutaexpress_notify.domain.ports.out.WarehouseTicketPort;
import org.springframework.stereotype.Component;

// Simula el sistema de bodega escribiendo el ticket en el log
@Slf4j
@Component
public class LogWarehouseTicketAdapter implements WarehouseTicketPort {

    @Override
    public void createPickingTicket(String shipmentId, String originAddress) {
        log.info("[TICKET] creado | envío={} | retirar en={}", shipmentId, originAddress);
    }

    @Override
    public void cancelPickingTicket(String shipmentId) {
        log.info("[TICKET] anulado | envío={}", shipmentId);
    }
}