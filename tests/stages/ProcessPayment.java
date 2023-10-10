package stages;

// Imports for E-Comm Demo
import ecomm.Order;
// Imports necessary for a Stage
import pipes.payloads.Payload;
import pipes.stages.StageInterface;
import pipes.stages.exceptions.StageException;

public class ProcessPayment implements StageInterface {

    @Override
    public Payload apply(Payload payload) throws StageException {
        System.out.printf("Stage %s: Processing payment for ecomm.Order in Payload.%n", ProcessPayment.class.getName());

        Order order = payload.getResult(Order.class);
        // Use to demo short-circuiting a stage.
        if(order.getOrderId() == 1002) {
            throw new StageException("Failed to process payment.", new Throwable());
        }
        order.setPaid(true);
        payload.setResult(Order.class, order);  // Since the order doesn't change, this is redundant.
        return payload;
    }
}
