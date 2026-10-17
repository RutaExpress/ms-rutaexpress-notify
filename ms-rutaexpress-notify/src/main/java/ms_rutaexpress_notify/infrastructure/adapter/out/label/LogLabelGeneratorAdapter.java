package ms_rutaexpress_notify.infrastructure.adapter.out.label;

import lombok.extern.slf4j.Slf4j;
import ms_rutaexpress_notify.domain.ports.out.LabelGeneratorPort;
import org.springframework.stereotype.Component;

// Simula la generación del PDF de la etiqueta escribiéndola en el log
@Slf4j
@Component
public class LogLabelGeneratorAdapter implements LabelGeneratorPort {

    @Override
    public void generateLabel(String shipmentId, String recipientName, String destinationAddress) {
        log.info("[ETIQUETA] generada | envío={} | para={} | dirección={}",
                shipmentId, recipientName, destinationAddress);
    }
}