package stages;

// Imports for E-Comm Demo
import ecomm.Order;
// Imports necessary for a Stage
import pipes.payloads.Payload;
import pipes.stages.StageInterface;

public class SendInvoice implements StageInterface {

    @Override
    public Payload apply(Payload payload) {
        System.out.printf("Stage %s: Sending Invoice for ecomm.Order just paid.%n", SendInvoice.class.getName());
        Order order = payload.getResult(Order.class);
        // Send invoice code here
        payload.setResult(Order.class, order); // This is again redundant
        return payload;
    }
}
