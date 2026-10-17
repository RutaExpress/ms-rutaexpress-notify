package ms_rutaexpress_notify.infrastructure.adapter.in.messaging.email;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import ms_rutaexpress_notify.domain.model.ShipmentEvent;
import ms_rutaexpress_notify.domain.ports.in.ProcessNotificationUseCase;
import ms_rutaexpress_notify.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Consumidor de la cola q.cmd.email (Guía 2.2.3, paso 2.4)
@Slf4j
@Component
public class EmailCommandListener {

    private final ProcessNotificationUseCase processNotificationUseCase; // lógica de negocio
    private final ObjectMapper objectMapper;                             // JSON -> ShipmentEvent

    // Spring inyecta el caso de uso y el convertidor JSON
    public EmailCommandListener(ProcessNotificationUseCase processNotificationUseCase,
                                ObjectMapper objectMapper) {
        this.processNotificationUseCase = processNotificationUseCase;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)  // escucha la cola de email
    public void onMessage(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();   // número del mensaje (Guía 2.1.2)
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("[RECIBIDO] cola={} deliveryTag={} body={}", RabbitMQConfig.EMAIL_QUEUE, tag, body);

        try {
            ShipmentEvent event = objectMapper.readValue(body, ShipmentEvent.class);
            processNotificationUseCase.process(event);   // "envía" el email
            channel.basicAck(tag, false);                 // ACK: todo bien -> RabbitMQ lo borra
            log.info("[ACK] deliveryTag={} eventId={}", tag, event.eventId());
        } catch (Exception e) {
            channel.basicNack(tag, false, false);         // NACK sin reencolar -> DLX -> DLQ
            log.error("[NACK->DLQ] deliveryTag={} motivo={}", tag, e.getMessage());
        }
    }
}