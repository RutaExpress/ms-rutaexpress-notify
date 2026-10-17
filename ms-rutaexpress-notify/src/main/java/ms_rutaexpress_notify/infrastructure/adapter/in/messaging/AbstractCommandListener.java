package ms_rutaexpress_notify.infrastructure.adapter.in.messaging;

import com.rabbitmq.client.Channel;
import ms_rutaexpress_notify.domain.exception.InvalidMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Template Method: define los pasos fijos de todo consumidor.
 * leer mensaje -> handle() (lo implementa cada hija) -> ACK o NACK.
 * Así el manejo de ACK/NACK se escribe una sola vez 
 */
public abstract class AbstractCommandListener {

    protected final Logger log = LoggerFactory.getLogger(getClass()); // log con el nombre de la clase hija
    private final ObjectMapper objectMapper;

    protected AbstractCommandListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Paso variable: qué hace cada consumidor con el mensaje
    protected abstract void handle(String body);

    // Pasos fijos: recibir -> procesar -> confirmar
    protected void processMessage(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();             // número del mensaje
        String queue = message.getMessageProperties().getConsumerQueue();       // cola de origen
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("[RECIBIDO] cola={} deliveryTag={} body={}", queue, tag, body);

        try {
            handle(body);
            channel.basicAck(tag, false);                  // ACK: todo bien -> se borra
            log.info("[ACK] cola={} deliveryTag={}", queue, tag);
        } catch (InvalidMessageException e) {
            channel.basicNack(tag, false, false);          // NACK: mensaje inválido -> DLX -> DLQ
            log.error("[NACK->DLQ] cola={} deliveryTag={} motivo={}", queue, tag, e.getMessage());
        } catch (Exception e) {
            channel.basicNack(tag, false, false);          // NACK: error inesperado -> DLQ (no se pierde)
            log.error("[NACK->DLQ] cola={} deliveryTag={} error inesperado={}", queue, tag, e.getMessage());
        }
    }

    // Convierte el JSON al tipo pedido; si está roto, es un error NO recuperable
    protected <T> T parse(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (Exception e) {
            throw new InvalidMessageException("JSON inválido: " + e.getMessage());
        }
    }
}