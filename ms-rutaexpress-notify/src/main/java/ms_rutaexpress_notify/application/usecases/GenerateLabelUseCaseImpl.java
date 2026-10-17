package ms_rutaexpress_notify.application.usecases;

import ms_rutaexpress_notify.domain.exception.InvalidMessageException;
import ms_rutaexpress_notify.domain.model.LabelCommand;
import ms_rutaexpress_notify.domain.ports.in.GenerateLabelUseCase;
import ms_rutaexpress_notify.domain.ports.out.LabelGeneratorPort;
import org.springframework.stereotype.Service;

// Caso de uso: valida y genera la etiqueta del envío
@Service
public class GenerateLabelUseCaseImpl implements GenerateLabelUseCase {

    private final LabelGeneratorPort labelGeneratorPort;

    public GenerateLabelUseCaseImpl(LabelGeneratorPort labelGeneratorPort) {
        this.labelGeneratorPort = labelGeneratorPort;
    }

    @Override
    public void process(LabelCommand command) {
        validate(command);
        labelGeneratorPort.generateLabel(command.shipmentId(), command.recipientName(), command.destinationAddress());
    }

    // Reglas del mensaje: errores NO recuperables
    private void validate(LabelCommand command) {
        if (command == null) {
            throw new InvalidMessageException("Mensaje vacío");
        }
        if (command.shipmentId() == null || command.shipmentId().isBlank()) {
            throw new InvalidMessageException("Falta shipmentId");
        }
        if (command.destinationAddress() == null || command.destinationAddress().isBlank()) {
            throw new InvalidMessageException("Falta destinationAddress para la etiqueta");
        }
    }
}