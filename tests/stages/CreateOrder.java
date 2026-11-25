package stages;

import ecomm.Order;
import pipes.stages.StageInterface;

public class CreateOrder implements StageInterface<Order> {

    @Override
    public Order apply(Order order) {
        System.out.printf("Stage %s: Creating Order %d.%n", CreateOrder.class.getName(), order.getOrderId());
        order.setProcessed(true); // Pretend we put the order in our system.
        return order;
    }
}
