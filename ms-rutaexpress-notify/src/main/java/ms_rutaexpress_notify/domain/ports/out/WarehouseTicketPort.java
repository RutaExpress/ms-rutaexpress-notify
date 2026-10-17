package ms_rutaexpress_notify.domain.ports.out;

// Puerto de salida: sistema de bodega
public interface WarehouseTicketPort {
    void createPickingTicket(String shipmentId, String originAddress);
    void cancelPickingTicket(String shipmentId);
}