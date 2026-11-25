package stages;

import ecomm.Order;
import pipes.stages.StageInterface;

public class SendInvoice implements StageInterface<Order> {

    @Override
    public Order apply(Order order) {
        System.out.printf("Stage %s: Sending Invoice for Order %d.%n", SendInvoice.class.getName(), order.getOrderId());
        // Send invoice code here
        return order;
    }
}
