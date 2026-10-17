package ms_rutaexpress_notify.application.usecases;

import ms_rutaexpress_notify.domain.exception.InvalidMessageException;
import ms_rutaexpress_notify.domain.model.TicketAction;
import ms_rutaexpress_notify.domain.model.WarehouseTicketCommand;
import ms_rutaexpress_notify.domain.ports.in.GenerateWarehouseTicketUseCase;
import ms_rutaexpress_notify.domain.ports.out.WarehouseTicketPort;
import org.springframework.stereotype.Service;

// Caso de uso: valida y crea o anula el ticket de picking
@Service
public class GenerateWarehouseTicketUseCaseImpl implements GenerateWarehouseTicketUseCase {

    private final WarehouseTicketPort warehouseTicketPort;

    public GenerateWarehouseTicketUseCaseImpl(WarehouseTicketPort warehouseTicketPort) {
        this.warehouseTicketPort = warehouseTicketPort;
    }

    @Override
    public void process(WarehouseTicketCommand command) {
        validate(command);
        switch (command.action()) {
            case CREATE -> warehouseTicketPort.createPickingTicket(command.shipmentId(), command.originAddress());
            case CANCEL -> warehouseTicketPort.cancelPickingTicket(command.shipmentId());
        }
    }

    // Reglas del mensaje: errores NO recuperables
    private void validate(WarehouseTicketCommand command) {
        if (command == null) {
            throw new InvalidMessageException("Mensaje vacío");
        }
        if (command.shipmentId() == null || command.shipmentId().isBlank()) {
            throw new InvalidMessageException("Falta shipmentId");
        }
        if (command.action() == null) {
            throw new InvalidMessageException("Falta action (CREATE o CANCEL)");
        }
        if (command.action() == TicketAction.CREATE
                && (command.originAddress() == null || command.originAddress().isBlank())) {
            throw new InvalidMessageException("Falta originAddress para crear el ticket");
        }
    }
}