package stages;

// Imports for E-Comm Demo
import ecomm.Order;
// Imports necessary for a Stage
import pipes.payloads.Payload;
import pipes.stages.StageInterface;

public class CreateOrder implements StageInterface {

    @Override
    public Payload apply(Payload payload) {
        System.out.printf("Stage %s: Creating an ecomm.Order with data from payload.%n", CreateOrder.class.getName());
        // Pretend we put the order in our system.
        Order order = payload.getResult(Order.class);
        order.setProcessed(true);
        payload.setResult(Order.class, order);  // This is redundant, but here for demonstration.
        return payload;
    }
}
