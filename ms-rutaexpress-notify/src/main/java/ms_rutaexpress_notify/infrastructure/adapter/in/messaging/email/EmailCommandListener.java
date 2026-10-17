package ms_rutaexpress_notify.infrastructure.adapter.in.messaging.email;

import com.rabbitmq.client.Channel;
import ms_rutaexpress_notify.domain.model.ShipmentEvent;
import ms_rutaexpress_notify.domain.ports.in.ProcessNotificationUseCase;
import ms_rutaexpress_notify.infrastructure.adapter.in.messaging.AbstractCommandListener;
import ms_rutaexpress_notify.infrastructure.config.RabbitMQConfig;
import ms_rutaexpress_notify.infrastructure.config.RetryProperties;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

// Consumidor de q.cmd.email: hereda el manejo de ACK/NACK de AbstractCommandListener
@Component
public class EmailCommandListener extends AbstractCommandListener {

    private final ProcessNotificationUseCase processNotificationUseCase;

    public EmailCommandListener(ProcessNotificationUseCase processNotificationUseCase,
                                ObjectMapper objectMapper,
                                RetryProperties retryProperties) {
        super(objectMapper, retryProperties);  // la clase padre maneja JSON y reintentos
        this.processNotificationUseCase = processNotificationUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void onMessage(Message message, Channel channel) throws IOException {
        processMessage(message, channel);  // pasos fijos de la clase padre
    }

    // Paso propio de email: JSON -> ShipmentEvent -> caso de uso
    @Override
    protected void handle(String body) {
        ShipmentEvent event = parse(body, ShipmentEvent.class);
        processNotificationUseCase.process(event);
    }
}