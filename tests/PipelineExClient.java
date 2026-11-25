// Imports for E-Comm Demo
import ecomm.Item;
import ecomm.Order;
// Imports from Design Pattern
import pipes.Pipeline;
import pipes.PipelineInterface;
import pipes.builder.PipelineBuilder;
import pipes.builder.PipelineBuilderInterface;
import pipes.processors.InterruptibleProcessor;
import pipes.stages.exceptions.StageException;
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
        PipelineInterface<Order> pipeline1 = new Pipeline<Order>()
                .pipe(new CreateOrder())
                .pipe(new ProcessPayment())
                .pipe(new SendInvoice())
                .pipe(new ExportOrder());
        Order order = pipeline1.process(new Order(1001, items));
        System.out.printf("----------------%n%s%n", order);

        // Example 2:
        // Do a little Math
        // Re-order-ability becomes easy-peasy.
        int result = new Pipeline<Integer>()
                .pipe(new AddOneStage())
                .pipe(new TimesTwoStage())
                .process(32);
        System.out.printf("----------------%nResult %d%n%n", result);

        // Example 3:
        // Short Circuit the processing by throwing an exception from a stage.
        items.add(new Item("Juvenile female pet panda", 1)); // Adding an item for demonstration purposes.
        order = new Order(1002, items);
        try {
            pipeline1.process(order);
        } catch (StageException se) {
            System.out.println(se.getMessage());
        } finally {
            System.out.printf("----------------%n%s%n", order);
        }

        // Example 4
        // Adding a Builder Pattern lets you conditionally add Stages
        order = new Order(1003, items, "New Zealand");
        PipelineBuilderInterface<Order> builder = new PipelineBuilder<Order>()
                .add(new CreateOrder());
        if (order.getOrigin().equals("New Zealand")) { // Conditionally added stage.
            builder.add(new PreBookCarrier());
        }
        builder.add(new ProcessPayment())
               .add(new SendInvoice())
               .add(new ExportOrder());
        PipelineInterface<Order> pipeline = builder.build(); // Uses the default FingersCrossedProcessor
        order = pipeline.process(order);
        System.out.printf("----------------%n%s%n", order);

        // Example 5
        // Pipe Re-Use: a pipeline is a stage, so it can be piped into another pipeline.
        PipelineInterface<Order> pipeline2 = new Pipeline<Order>()
                .pipe(new CreateOrder())
                .pipe(new GenerateInvoice());
        PipelineInterface<Order> pipeline3 = new Pipeline<Order>()
                .pipe(pipeline2)  // Pipeline Re-Use ;-)
                .pipe(new ProcessPayment())
                .pipe(new SendInvoice())
                .pipe(new ExportOrder());
        order = pipeline3.process(new Order(1004, items));
        System.out.printf("----------------%n%s%n", order);

        // Example 6
        // Stages don't have to be classes: lambdas and method references work too.
        String greeting = new Pipeline<String>()
                .pipe(String::trim)
                .pipe(s -> s.replace("php", "Java"))
                .pipe(String::toUpperCase)
                .process("   hello php pipelines   ");
        System.out.printf("----------------%nResult %s%n%n", greeting);

        // Example 7
        // Short Circuit without exceptions: the InterruptibleProcessor stops as soon as its check fails.
        int interrupted = new Pipeline<Integer>(new InterruptibleProcessor<>(p -> p < 100))
                .pipe(new TimesTwoStage())  // 64  -> keep going
                .pipe(new TimesTwoStage())  // 128 -> check fails, stop here
                .pipe(new AddOneStage())    // never runs
                .process(32);
        System.out.printf("----------------%nResult %d%n%n", interrupted);

        // Example 8
        // Pipelines are immutable: pipe() returns a NEW pipeline and leaves the original untouched.
        PipelineInterface<Integer> timesTwo = new Pipeline<Integer>().pipe(new TimesTwoStage());
        PipelineInterface<Integer> timesTwoPlusOne = timesTwo.pipe(new AddOneStage());
        System.out.printf("----------------%ntimesTwo(10) = %d, timesTwoPlusOne(10) = %d%n",
                timesTwo.process(10), timesTwoPlusOne.process(10));
    }
}
