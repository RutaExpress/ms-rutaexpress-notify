package ms_rutaexpress_notify.domain.exception;

// Error NO recuperable: el mensaje viene mal y reintentarlo no sirve -> NACK -> DLQ
public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException(String message) {
        super(message);
    }
}