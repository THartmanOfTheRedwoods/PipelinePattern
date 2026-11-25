package stages;

import ecomm.Order;
import pipes.stages.StageInterface;
import pipes.stages.exceptions.StageException;

public class ProcessPayment implements StageInterface<Order> {

    @Override
    public Order apply(Order order) {
        System.out.printf("Stage %s: Processing payment for Order %d.%n", ProcessPayment.class.getName(), order.getOrderId());
        // Used to demo short-circuiting a pipeline.
        if (order.getOrderId() == 1002) {
            throw new StageException("Failed to process payment for Order " + order.getOrderId() + ".");
        }
        order.setPaid(true);
        return order;
    }
}
