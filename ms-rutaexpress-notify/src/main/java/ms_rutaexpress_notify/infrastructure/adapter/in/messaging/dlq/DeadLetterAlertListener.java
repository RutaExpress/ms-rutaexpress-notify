package ms_rutaexpress_notify.infrastructure.adapter.in.messaging.dlq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import ms_rutaexpress_notify.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Monitor de las 3 DLQ: registra una alerta por cada mensaje fallido 
// Se puede apagar con notify.dlq-alert.enabled=false para dejar los mensajes visibles en la DLQ
@Slf4j
@Component
public class DeadLetterAlertListener {

    @RabbitListener(
            queues = {RabbitMQConfig.EMAIL_DLQ, RabbitMQConfig.WAREHOUSE_DLQ, RabbitMQConfig.LABEL_DLQ},
            autoStartup = "${notify.dlq-alert.enabled:true}")
    public void onDeadLetter(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        var headers = message.getMessageProperties().getHeaders();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        log.warn("[DLQ-ALERTA] dlq={} colaOrigen={} motivo={} body={} -> revisar",
                message.getMessageProperties().getConsumerQueue(),
                headers.get("x-first-death-queue"),     // cola donde falló
                headers.get("x-first-death-reason"),    // rejected / expired / maxlen
                body);

        channel.basicAck(tag, false);  // alerta registrada
    }
}