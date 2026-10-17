package ms_rutaexpress_notify.domain.exception;

// Error RECUPERABLE: falla temporal externa (ej. servidor de correo caído) -> se reintenta
public class TemporaryFailureException extends RuntimeException {
    public TemporaryFailureException(String message) {
        super(message);
    }
}