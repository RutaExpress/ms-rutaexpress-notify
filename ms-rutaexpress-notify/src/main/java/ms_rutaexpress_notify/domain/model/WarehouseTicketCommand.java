package ms_rutaexpress_notify.domain.model;

// Mensaje de la cola q.cmd.warehouse
public record WarehouseTicketCommand(
        String eventId,
        String shipmentId,
        String originAddress,   // dónde se retira el paquete
        TicketAction action     // CREATE o CANCEL (un valor distinto -> JSON inválido)
) {}