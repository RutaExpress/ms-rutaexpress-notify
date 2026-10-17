package ms_rutaexpress_notify.infrastructure.adapter.in.messaging.warehouse;

import com.rabbitmq.client.Channel;
import ms_rutaexpress_notify.domain.model.WarehouseTicketCommand;
import ms_rutaexpress_notify.domain.ports.in.GenerateWarehouseTicketUseCase;
import ms_rutaexpress_notify.infrastructure.adapter.in.messaging.AbstractCommandListener;
import ms_rutaexpress_notify.infrastructure.config.RabbitMQConfig;
import ms_rutaexpress_notify.infrastructure.config.RetryProperties;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

// Consumidor de q.cmd.warehouse (dominio bodega): hereda ACK/NACK/reintentos
@Component
public class WarehouseTicketListener extends AbstractCommandListener {

    private final GenerateWarehouseTicketUseCase generateWarehouseTicketUseCase;

    public WarehouseTicketListener(GenerateWarehouseTicketUseCase generateWarehouseTicketUseCase,
                                   ObjectMapper objectMapper,
                                   RetryProperties retryProperties) {
        super(objectMapper, retryProperties);
        this.generateWarehouseTicketUseCase = generateWarehouseTicketUseCase;
    }

    @RabbitListener(queues = RabbitMQConfig.WAREHOUSE_QUEUE)
    public void onMessage(Message message, Channel channel) throws IOException {
        processMessage(message, channel);
    }

    @Override
    protected void handle(String body) {
        generateWarehouseTicketUseCase.process(parse(body, WarehouseTicketCommand.class));
    }
}