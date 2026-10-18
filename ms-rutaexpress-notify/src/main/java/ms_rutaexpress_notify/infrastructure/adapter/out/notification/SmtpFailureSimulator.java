package ms_rutaexpress_notify.infrastructure.adapter.out.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

// Interruptor de prueba de resiliencia: los primeros N envíos de email fallan (0 = apagado).
// Se activa solo por variable de entorno NOTIFY_SMTP_FAIL_FIRST (sin endpoint).
@Slf4j
@Component
public class SmtpFailureSimulator {

    private final AtomicInteger pendingFailures;

    public SmtpFailureSimulator(@Value("${notify.smtp.fail-first:0}") int failFirst) {
        this.pendingFailures = new AtomicInteger(Math.max(failFirst, 0));
        if (failFirst > 0) {
            log.warn("[SIMULACION] activa: los primeros {} envíos de email fallarán", failFirst);
        }
    }

    // true si este envío debe fallar (y descuenta una falla)
    public boolean shouldFail() {
        return pendingFailures.getAndUpdate(n -> n > 0 ? n - 1 : 0) > 0;
    }
}
