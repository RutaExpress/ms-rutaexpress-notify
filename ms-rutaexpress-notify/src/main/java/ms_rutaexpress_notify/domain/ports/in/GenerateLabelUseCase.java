package ms_rutaexpress_notify.domain.ports.in;

import ms_rutaexpress_notify.domain.model.LabelCommand;

// Puerto de entrada: generar la etiqueta del envío
public interface GenerateLabelUseCase {
    void process(LabelCommand command);
}