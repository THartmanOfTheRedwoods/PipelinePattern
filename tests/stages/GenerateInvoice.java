package stages;

// Imports for E-Comm Demo
import ecomm.Order;
// Imports necessary for Stage
import pipes.payloads.Payload;
import pipes.stages.StageInterface;

public class GenerateInvoice implements StageInterface {

    @Override
    public Payload apply(Payload payload) {
        System.out.printf("Stage %s: Generating Invoice for ecomm.Order just paid.%n", GenerateInvoice.class.getName());
        Order order = payload.getResult(Order.class);
        // Generate invoice code here
        payload.setResult(Order.class, order); // This is again redundant
        return payload;
    }
}
