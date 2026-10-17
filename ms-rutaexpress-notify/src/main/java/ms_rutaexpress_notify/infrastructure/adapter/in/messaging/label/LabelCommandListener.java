package ms_rutaexpress_notify.infrastructure.adapter.in.messaging.label;

import com.rabbitmq.client.Channel;
import ms_rutaexpress_notify.domain.model.LabelCommand;
import ms_rutaexpress_notify.domain.ports.in.GenerateLabelUseCase;
import ms_rutaexpress_notify.infrastructure.adapter.in.messaging.AbstractCommandListener;
import ms_rutaexpress_notify.infrastructure.config.RabbitMQConfig;
import ms_rutaexpress_notify.infrastructure.config.RetryProperties;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

// Consumidor de q.cmd.label (dominio etiquetas): hereda ACK/NACK/reintentos
@Component
public class LabelCommandListener extends AbstractCommandListener {

    private final GenerateLabelUseCase generateLabelUseCase;

    public LabelCommandListener(GenerateLabelUseCase generateLabelUseCase,
                                ObjectMapper objectMapper,
                                RetryProperties retryProperties) {
        super(objectMapper, retryProperties);
        this.generateLabelUseCase = generateLabelUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.LABEL_QUEUE)
    public void onMessage(Message message, Channel channel) throws IOException {
        processMessage(message, channel);
    }

    @Override
    protected void handle(String body) {
        generateLabelUseCase.process(parse(body, LabelCommand.class));
    }
}