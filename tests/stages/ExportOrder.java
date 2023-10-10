package stages;

// Imports for E-Comm Demo
import ecomm.Order;
// Imports necessary for a Stage
import pipes.payloads.Payload;
import pipes.stages.StageInterface;

public class ExportOrder implements StageInterface {

    @Override
    public Payload apply(Payload payload) {
        System.out.printf("Stage %s: Transmitting ecomm.Order data from payload to Drop-Ship Vendor.%n", ExportOrder.class.getName());
        Order order = payload.getResult(Order.class);
        // Transmit order code here
        payload.setResult(Order.class, order);
        return payload;
    }
}
