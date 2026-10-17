package ms_rutaexpress_notify.domain.model;

// Mensaje que llega por la cola q.cmd.email (JSON -> record inmutable)
public record ShipmentEvent(
        String eventId,        // id único del mensaje (para seguirlo en los logs)
        String shipmentId,     // envío al que se refiere
        String status,         // estado del envío (ACEPTADO, EN_RUTA, ENTREGADO...)
        String customerEmail,  // email del destinatario
        String message         // texto adicional
) {}