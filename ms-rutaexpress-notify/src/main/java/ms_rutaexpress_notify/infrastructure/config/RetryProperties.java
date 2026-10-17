package ms_rutaexpress_notify.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Política de reintentos leída desde application.yaml (notify.retry.*)
@ConfigurationProperties(prefix = "notify.retry")
public record RetryProperties(
        int maxAttempts,        // intentos totales
        long initialInterval,   // espera antes del 2.º intento (ms)
        double multiplier,      // cada espera se multiplica por este valor
        long maxInterval        // espera máxima (ms)
) {}