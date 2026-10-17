package ms_rutaexpress_notify.domain.model;

import java.io.Serializable;

public class ShipmentEvent implements Serializable {
    private String shipmentId;
    private String status;
    private String customerEmail;
    private String message;

    // Constructores vacíos y con parámetros (necesarios para la deserialización JSON)
    public ShipmentEvent() {}

    public ShipmentEvent(String shipmentId, String status, String customerEmail, String message) {
        this.shipmentId = shipmentId;
        this.status = status;
        this.customerEmail = customerEmail;
        this.message = message;
    }

    // Getters y Setters
    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "ShipmentEvent{" +
                "shipmentId='" + shipmentId + '\'' +
                ", status='" + status + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}