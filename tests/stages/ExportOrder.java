package stages;

import ecomm.Order;
import pipes.stages.StageInterface;

public class ExportOrder implements StageInterface<Order> {

    @Override
    public Order apply(Order order) {
        System.out.printf("Stage %s: Transmitting Order %d to the Drop-Ship Vendor.%n", ExportOrder.class.getName(), order.getOrderId());
        // Transmit order code here
        return order;
    }
}
