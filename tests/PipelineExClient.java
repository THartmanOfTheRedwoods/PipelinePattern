// Imports for E-Comm Demo
import ecomm.Item;
import ecomm.Order;
// Imports from Design Pattern
import pipes.Pipeline;
import pipes.payloads.Payload;
import pipes.PipelineInterface;
import pipes.builder.PipelineBuilderInterface;
import pipes.builder.PipelineBuilder;
import pipes.stages.exceptions.StageException;
import pipes.processors.FingersCrossedProcessor;
// Import custom stages used in this example client.
import stages.*;
// Import utility data-structures
import java.util.ArrayList;
import java.util.List;

// TODO: Make a client that can dynamically build and execute pipelines and stages from a json file.
public class PipelineExClient {
    public static void main(String[] args) {
        // Helper item list for Order demo.
        List<Item> items = new ArrayList<>();
        items.add(new Item("10 foot chain-link fence", 3));
        items.add(new Item("10 foot chain-link gate", 1));
        items.add(new Item("Juvenile male pet panda", 1));

        // Example 1:
        // BETTER READABILITY: No long or nested if-else structures    //
        // BETTER TESTABILITY: Stages are easily duplicated and mocked //
        // SINGLE RESPONSIBILITY: One Stage, One job                   //
        Payload order = new Payload(Order.class, new Order(1001, items));
        PipelineInterface pipeline1 = new Pipeline()
                .pipe(new CreateOrder())
                .pipe(new ProcessPayment())
                .pipe(new SendInvoice())
                .pipe(new ExportOrder());
        pipeline1.process(order);
        System.out.printf("----------------%n%s%n", order.getResult(Order.class));

        // Example 2:
        // Do a little Math
        // Re-order-ability becomes easy-peasy.
        int result = new Pipeline()
                .pipe(new AddOneStage())
                .pipe(new TimesTwoStage())
                .process(new Payload(Integer.class, 32)).getResult(Integer.class);
        System.out.printf("%n----------------%nResult %d%n%n", result);

        // Example 3:
        // Short Circuit the processing
        items.add(new Item("Juvenile female pet panda", 1)); // Adding an item for demonstration purposes.
        order = new Payload(Order.class, new Order(1002, items));
        try {
            pipeline1.process(order);
        } catch(StageException se) {
                System.out.println(se.getMessage());
        } finally {
            System.out.printf("----------------%n%s%n", order.getResult(Order.class));
        }

        // Example 4
        // Adding a Builder Pattern lets you conditionally add Stages
        PipelineBuilderInterface pb = new PipelineBuilder();
        pb = pb.add(new TimesTwoStage()).add(new AddOneStage());
        if(true) { // Conditionally added stage.
            pb.add(new TimesTwoStage());
        }
        PipelineInterface pipeline = pb.build(new FingersCrossedProcessor());
        Payload p = new Payload(Integer.class, 32);
        pipeline.process(p);

        System.out.printf("%n----------------%nResult %d%n%n", p.getResult(Integer.class));

        // Example 5
        // Pipe Re-Use
        order = new Payload(Order.class, new Order(1003, items));
        PipelineInterface pipeline2 = new Pipeline()
                .pipe(new CreateOrder())
                .pipe(new GenerateInvoice());
        PipelineInterface pipeline3 = new Pipeline()
                .pipe(pipeline2)  // Pipeline Re-Use ;-)
                .pipe(new ProcessPayment())
                .pipe(new SendInvoice())
                .pipe(new ExportOrder());
        pipeline3.process(order);
        System.out.printf("----------------%n%s%n", order.getResult(Order.class));
    }
}
