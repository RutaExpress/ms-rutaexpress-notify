package ms_rutaexpress_notify.domain.ports.out;

// Puerto de salida: generador de etiquetas (PDF)
public interface LabelGeneratorPort {
    void generateLabel(String shipmentId, String recipientName, String destinationAddress);
}