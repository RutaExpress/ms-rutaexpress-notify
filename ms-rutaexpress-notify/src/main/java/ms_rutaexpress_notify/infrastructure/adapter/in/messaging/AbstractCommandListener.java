package ms_rutaexpress_notify.infrastructure.adapter.in.messaging;

import com.rabbitmq.client.Channel;
import ms_rutaexpress_notify.domain.exception.InvalidMessageException;
import ms_rutaexpress_notify.domain.exception.TemporaryFailureException;
import ms_rutaexpress_notify.infrastructure.config.RetryProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Template Method: pasos fijos de todo consumidor.
 * leer -> handle() con reintentos si la falla es temporal -> ACK o NACK.
 * No recuperable (InvalidMessageException) -> NACK inmediato -> DLQ.
 * Recuperable (TemporaryFailureException) -> reintenta; si se agotan -> NACK -> DLQ.
 */
public abstract class AbstractCommandListener {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final RetryProperties retry;

    protected AbstractCommandListener(ObjectMapper objectMapper, RetryProperties retry) {
        this.objectMapper = objectMapper;
        this.retry = retry;
    }

    // Paso variable: qué hace cada consumidor con el mensaje
    protected abstract void handle(String body);

    // Pasos fijos: recibir -> procesar (con reintentos) -> confirmar
    protected void processMessage(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        String queue = message.getMessageProperties().getConsumerQueue();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("[RECIBIDO] cola={} deliveryTag={} body={}", queue, tag, body);

        try {
            handleWithRetry(body, queue, tag);
            channel.basicAck(tag, false);                  // ACK: todo bien -> se borra
            log.info("[ACK] cola={} deliveryTag={}", queue, tag);
        } catch (InvalidMessageException e) {
            channel.basicNack(tag, false, false);          // NACK: no recuperable -> DLQ
            log.error("[NACK->DLQ] cola={} deliveryTag={} motivo={}", queue, tag, e.getMessage());
        } catch (TemporaryFailureException e) {
            channel.basicNack(tag, false, false);          // NACK: reintentos agotados -> DLQ
            log.error("[NACK->DLQ] cola={} deliveryTag={} reintentos agotados ({}/{}) motivo={}",
                    queue, tag, retry.maxAttempts(), retry.maxAttempts(), e.getMessage());
        } catch (Exception e) {
            channel.basicNack(tag, false, false);          // NACK: error inesperado -> DLQ
            log.error("[NACK->DLQ] cola={} deliveryTag={} error inesperado={}", queue, tag, e.getMessage());
        }
    }

    // Reintenta solo las fallas temporales, con espera creciente (1 s, 2 s, 4 s...)
    private void handleWithRetry(String body, String queue, long tag) {
        long wait = retry.initialInterval();
        for (int attempt = 1; ; attempt++) {
            try {
                handle(body);
                return;                                    // salió bien
            } catch (TemporaryFailureException e) {
                if (attempt >= retry.maxAttempts()) {
                    throw e;                               // se agotaron -> NACK en processMessage
                }
                log.warn("[REINTENTO {}/{}] cola={} deliveryTag={} esperando {} ms motivo={}",
                        attempt, retry.maxAttempts(), queue, tag, wait, e.getMessage());
                sleep(wait);
                wait = Math.min((long) (wait * retry.multiplier()), retry.maxInterval());
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TemporaryFailureException("Reintento interrumpido");
        }
    }

    // JSON -> objeto; si está roto es un error NO recuperable
    protected <T> T parse(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (Exception e) {
            throw new InvalidMessageException("JSON inválido: " + e.getMessage());
        }
    }
}