package ms_rutaexpress_notify.infrastructure.adapter.in.rest.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * SOLO DESARROLLO LOCAL (perfil "dev").
 * Publica mensajes en RabbitMQ desde Postman para probar los consumidores.
 * Basado en la Guía 2.1.2, paso 9.
 */
@RestController
@RequestMapping("/api/test")
@Profile("dev")
public class DevTestController {

    private static final Logger log = LoggerFactory.getLogger(DevTestController.class);
    private final RabbitTemplate rabbitTemplate;

    public DevTestController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping(value = "/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> publish(
            @RequestParam(defaultValue = "") String exchange,
            @RequestParam String routingKey,
            @RequestBody String body) {

        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setContentEncoding(StandardCharsets.UTF_8.name());
        Message message = new Message(body.getBytes(StandardCharsets.UTF_8), props);

        rabbitTemplate.send(exchange, routingKey, message);
        log.info("[PUBLICADO] exchange='{}' key='{}'", exchange, routingKey);

        return ResponseEntity.accepted().body(Map.of(
                "estado", "publicado",
                "exchange", exchange,
                "routingKey", routingKey));
    }
}