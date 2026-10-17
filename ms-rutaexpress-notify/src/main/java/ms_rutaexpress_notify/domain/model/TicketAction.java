package ms_rutaexpress_notify.domain.model;

// Acción sobre el ticket de bodega
public enum TicketAction {
    CREATE,   // crear ticket de picking (envío ACEPTADO)
    CANCEL    // anular ticket (envío CANCELADO)
}