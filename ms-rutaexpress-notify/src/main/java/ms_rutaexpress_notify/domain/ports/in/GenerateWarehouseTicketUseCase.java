package ms_rutaexpress_notify.domain.ports.in;

import ms_rutaexpress_notify.domain.model.WarehouseTicketCommand;

// Puerto de entrada: generar o anular el ticket de bodega
public interface GenerateWarehouseTicketUseCase {
    void process(WarehouseTicketCommand command);
}