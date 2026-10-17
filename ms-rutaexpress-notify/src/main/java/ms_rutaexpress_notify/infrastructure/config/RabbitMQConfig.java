package ms_rutaexpress_notify.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología RabbitMQ (Caso sección 8). Guías 2.1.3 y 2.2.3.
 * Exchange -> (binding + routing key) -> Cola -> si falla (NACK) -> DLX -> DLQ
 */
@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String DIRECT_EXCHANGE = "cmd.direct";   // key exacta
    public static final String TOPIC_EXCHANGE  = "cmd.topic";    // key por patrón (* y #)
    public static final String DLX_EXCHANGE    = "cmd.dead.dlx"; // recibe los mensajes fallidos

    // Colas principales
    public static final String EMAIL_QUEUE     = "q.cmd.email";
    public static final String WAREHOUSE_QUEUE = "q.cmd.warehouse";
    public static final String LABEL_QUEUE     = "q.cmd.label";

    // DLQ (guardan los mensajes fallidos)
    public static final String EMAIL_DLQ     = "q.cmd.email.dlq";
    public static final String WAREHOUSE_DLQ = "q.cmd.warehouse.dlq";
    public static final String LABEL_DLQ     = "q.cmd.label.dlq";

    // Routing keys (direct)
    public static final String RK_EMAIL_SEND       = "email.send";
    public static final String RK_WAREHOUSE_TICKET = "warehouse.ticket";
    public static final String RK_LABEL_GEN        = "label.gen";

    // Patrones (topic): * = una palabra, # = cero o más
    public static final String PATTERN_EMAIL     = "email.*";
    public static final String PATTERN_WAREHOUSE = "warehouse.#";
    public static final String PATTERN_LABEL     = "label.*";

    // Routing keys hacia la DLX (deciden a qué DLQ va cada fallido)
    public static final String RK_EMAIL_DEAD     = "email.dead";
    public static final String RK_WAREHOUSE_DEAD = "warehouse.dead";
    public static final String RK_LABEL_DEAD     = "label.dead";

    // Retención (Guía 2.2.3)
    private static final int MAIN_QUEUE_TTL_MS     = 3_600_000;  // 1 hora
    private static final int MAIN_QUEUE_MAX_LENGTH = 1_000;
    private static final int DLQ_TTL_MS            = 86_400_000; // 24 horas

    // ---------- Exchanges (durable = true, autoDelete = false) ----------
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean // DLX direct (caso): cada fallido va solo a su DLQ
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // ---------- Colas principales ----------
    @Bean
    public Queue emailQueue() {
        return mainQueue(EMAIL_QUEUE, RK_EMAIL_DEAD);
    }

    @Bean
    public Queue warehouseQueue() {
        return mainQueue(WAREHOUSE_QUEUE, RK_WAREHOUSE_DEAD);
    }

    @Bean
    public Queue labelQueue() {
        return mainQueue(LABEL_QUEUE, RK_LABEL_DEAD);
    }

    // ---------- DLQ ----------
    @Bean
    public Queue emailDlq() {
        return deadLetterQueue(EMAIL_DLQ);
    }

    @Bean
    public Queue warehouseDlq() {
        return deadLetterQueue(WAREHOUSE_DLQ);
    }

    @Bean
    public Queue labelDlq() {
        return deadLetterQueue(LABEL_DLQ);
    }

    // ---------- Bindings direct ----------
    @Bean
    public Binding emailDirectBinding() {
        return BindingBuilder.bind(emailQueue()).to(directExchange()).with(RK_EMAIL_SEND);
    }

    @Bean
    public Binding warehouseDirectBinding() {
        return BindingBuilder.bind(warehouseQueue()).to(directExchange()).with(RK_WAREHOUSE_TICKET);
    }

    @Bean
    public Binding labelDirectBinding() {
        return BindingBuilder.bind(labelQueue()).to(directExchange()).with(RK_LABEL_GEN);
    }

    // ---------- Bindings topic ----------
    @Bean
    public Binding emailTopicBinding() {
        return BindingBuilder.bind(emailQueue()).to(topicExchange()).with(PATTERN_EMAIL);
    }

    @Bean
    public Binding warehouseTopicBinding() {
        return BindingBuilder.bind(warehouseQueue()).to(topicExchange()).with(PATTERN_WAREHOUSE);
    }

    @Bean
    public Binding labelTopicBinding() {
        return BindingBuilder.bind(labelQueue()).to(topicExchange()).with(PATTERN_LABEL);
    }

    // ---------- Bindings DLX -> DLQ ----------
    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq()).to(deadLetterExchange()).with(RK_EMAIL_DEAD);
    }

    @Bean
    public Binding warehouseDlqBinding() {
        return BindingBuilder.bind(warehouseDlq()).to(deadLetterExchange()).with(RK_WAREHOUSE_DEAD);
    }

    @Bean
    public Binding labelDlqBinding() {
        return BindingBuilder.bind(labelDlq()).to(deadLetterExchange()).with(RK_LABEL_DEAD);
    }

    // ---------- Apoyo ----------
    // Cola principal: si un mensaje se rechaza (NACK) o vence (TTL), va a la DLX
    private Queue mainQueue(String name, String deadLetterRoutingKey) {
        return QueueBuilder.durable(name)
                .withArgument("x-message-ttl", MAIN_QUEUE_TTL_MS)
                .withArgument("x-max-length", MAIN_QUEUE_MAX_LENGTH)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", deadLetterRoutingKey)
                .build();
    }

    // DLQ: guarda los fallidos 24 horas
    private Queue deadLetterQueue(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-message-ttl", DLQ_TTL_MS)
                .build();
    }
}