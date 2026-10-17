package ms_rutaexpress_notify.infrastructure.adapter.out.notification;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

// Simula caídas del servidor de correo: los próximos N envíos fallan (0 = funciona normal)
@Component
public class SmtpFailureSimulator {

    private final AtomicInteger pendingFailures = new AtomicInteger(0);

    // Programa N fallas seguidas
    public void failNext(int count) {
        pendingFailures.set(Math.max(count, 0));
    }

    // true si este envío debe fallar (y descuenta una falla)
    public boolean shouldFail() {
        return pendingFailures.getAndUpdate(n -> n > 0 ? n - 1 : 0) > 0;
    }
}