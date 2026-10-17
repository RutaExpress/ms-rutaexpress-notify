package ms_rutaexpress_notify.domain.model;

// Mensaje de la cola q.cmd.label
public record LabelCommand(
        String eventId,
        String shipmentId,
        String recipientName,       // nombre del destinatario
        String destinationAddress   // dirección que va impresa en la etiqueta
) {}