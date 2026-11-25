package stages;

import ecomm.Order;
import pipes.stages.StageInterface;

public class PreBookCarrier implements StageInterface<Order> {

    @Override
    public Order apply(Order order) {
        System.out.printf("Stage %s: Pre-booking an international carrier for Order %d.%n", PreBookCarrier.class.getName(), order.getOrderId());
        // Pre-book the carrier code here
        return order;
    }
}
