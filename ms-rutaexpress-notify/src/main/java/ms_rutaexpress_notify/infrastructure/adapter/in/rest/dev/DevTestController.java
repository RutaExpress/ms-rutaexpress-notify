package ms_rutaexpress_notify.infrastructure.adapter.in.rest.dev;

import ms_rutaexpress_notify.infrastructure.adapter.out.notification.SmtpFailureSimulator;
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
 * Publica mensajes en RabbitMQ desde Postman y simula fallas del servidor de correo.
 */
@RestController
@RequestMapping("/api/test")
@Profile("dev")
public class DevTestController {

    private static final Logger log = LoggerFactory.getLogger(DevTestController.class);
    private final RabbitTemplate rabbitTemplate;                 // productor (envía a RabbitMQ)
    private final SmtpFailureSimulator smtpFailureSimulator;     // simula caídas del correo

    public DevTestController(RabbitTemplate rabbitTemplate, SmtpFailureSimulator smtpFailureSimulator) {
        this.rabbitTemplate = rabbitTemplate;
        this.smtpFailureSimulator = smtpFailureSimulator;
    }

    // Publica un mensaje JSON en el exchange y routing key indicados
    @PostMapping(value = "/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> publish(
            @RequestParam(defaultValue = "") String exchange,   // "" = exchange por defecto
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

    // Simula que el servidor de correo falla en los próximos N envíos (0 = normal)
    @PostMapping("/smtp/fail-next")
    public ResponseEntity<Map<String, Object>> failNext(@RequestParam int count) {
        smtpFailureSimulator.failNext(count);
        log.info("[SIMULACION] el servidor de correo fallará en los próximos {} envíos", count);
        return ResponseEntity.ok(Map.of("fallasProgramadas", count));
    }
}