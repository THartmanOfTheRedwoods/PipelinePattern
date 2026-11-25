package stages;

import ecomm.Order;
import pipes.stages.StageInterface;

public class GenerateInvoice implements StageInterface<Order> {

    @Override
    public Order apply(Order order) {
        System.out.printf("Stage %s: Generating Invoice for Order %d.%n", GenerateInvoice.class.getName(), order.getOrderId());
        // Generate invoice code here
        return order;
    }
}
