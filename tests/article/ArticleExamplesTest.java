package article;

import article.legacy.OrderService;
import ecomm.Item;
import ecomm.Order;
import org.junit.jupiter.api.Test;
import pipes.Pipeline;
import pipes.PipelineInterface;
import pipes.builder.PipelineBuilder;
import pipes.builder.PipelineBuilderInterface;
import pipes.processors.InterruptibleProcessor;
import pipes.stages.exceptions.StageException;
import stages.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs every code example from THE_PIPELINE_PATTERN.md. The snippets inside these methods are
 * copied verbatim from the article; {@link ArticleSnippetsTest} fails if they drift apart.
 */
class ArticleExamplesTest {

    private static Order newOrder(long id, String origin) {
        List<Item> items = new ArrayList<>(List.of(new Item("10 foot chain-link fence", 3)));
        return new Order(id, items, origin);
    }

    @Test
    void theUglyVersion() {
        List<OrderService.Order> sent = new ArrayList<>();
        OrderService service = new OrderService(sent::add);
        OrderService.Order order = () -> "success";

        service.complete(order);

        assertEquals(List.of(order), sent);
    }

    @Test
    void nowConsiderThis() {
        Order order = newOrder(1001, "United States");

        PipelineInterface<Order> pipeline = new Pipeline<Order>()
                .pipe(new CreateOrder())
                .pipe(new ProcessPayment())
                .pipe(new SendInvoice())
                .pipe(new ExportOrder());

        pipeline.process(order);

        assertTrue(order.isProcessed());
        assertTrue(order.isPaid());
    }

    @Test
    void theResult() {
        RunAllTheThings runner = new RunAllTheThings();
        Payload result = runner.doIt();
        System.out.println(result.getResult());

        assertEquals("allthethings", result.getResult());
    }

    @Test
    void lambdaStagesMixedWithClassStages() {
        PipelineInterface<Payload> pipeline = new Pipeline<Payload>()
                .pipe(new DoStage1())
                .pipe(payload -> {
                    payload.setResult(payload.getResult() + "the");
                    return payload;
                })
                .pipe(new DoStage3());

        assertEquals("allthethings", pipeline.process(new Payload()).getResult());
    }

    @Test
    void stringPayloadOneLiners() {
        String result = new Pipeline<String>()
                .pipe(s -> s + "all")
                .pipe(s -> s + "the")
                .pipe(s -> s + "things")
                .pipe(String::toUpperCase)
                .process("");

        assertEquals("ALLTHETHINGS", result);
    }

    @Test
    void immutability() {
        PipelineInterface<Integer> timesTwo = new Pipeline<Integer>()
                .pipe(p -> p * 2);
        PipelineInterface<Integer> timesTwoPlusOne = timesTwo
                .pipe(p -> p + 1);

        timesTwo.process(10);        // 20 - the original pipeline is unchanged
        timesTwoPlusOne.process(10); // 21

        assertEquals(20, timesTwo.process(10));
        assertEquals(21, timesTwoPlusOne.process(10));
    }

    @Test
    void shortCircuitingWithAnException() {
        Order order = newOrder(1002, "United States");
        PipelineInterface<Order> pipeline = new Pipeline<Order>()
                .pipe(new CreateOrder())
                .pipe(new ProcessPayment())
                .pipe(new SendInvoice());

        try {
            pipeline.process(order);
        } catch (StageException e) {
            // Do something else!
        }

        assertThrows(StageException.class, () -> pipeline.process(order));
        assertTrue(order.isProcessed());
        assertFalse(order.isPaid(), "stages after the failing one must not run");
    }

    @Test
    void shortCircuitingWithTheInterruptibleProcessor() {
        PipelineInterface<Integer> pipeline = new Pipeline<Integer>(new InterruptibleProcessor<>(p -> p < 100))
                .pipe(p -> p * 2)
                .pipe(p -> p * 2)
                .pipe(p -> p + 1);

        pipeline.process(32); // 128: the check fails after the second stage, so the third never runs

        assertEquals(128, pipeline.process(32));
    }

    @Test
    void dynamicPipelines() {
        for (String origin : List.of("New Zealand", "United States")) {
            Order order = newOrder(1003, origin);

            // Instantiate the PipelineBuilder
            PipelineBuilderInterface<Order> builder = new PipelineBuilder<Order>()
                    .add(new CreateOrder());

            // Conditional stage
            if (order.getOrigin().equals("New Zealand")) {
                builder.add(new PreBookCarrier());
            }

            // Continue adding more stages
            builder.add(new ProcessPayment())
                   .add(new SendInvoice())
                   .add(new ExportOrder());

            // Assemble the pipeline
            PipelineInterface<Order> pipeline = builder.build();

            // Process
            pipeline.process(order);

            assertTrue(order.isPaid());
        }
    }

    @Test
    void buildWithAProcessor() {
        PipelineBuilderInterface<Order> builder = new PipelineBuilder<Order>()
                .add(o -> o)                  // order is not processed yet -> stop here
                .add(new CreateOrder());
        Order order = newOrder(1005, "United States");

        builder.build(new InterruptibleProcessor<>(Order::isProcessed)).process(order);

        assertFalse(order.isProcessed());
    }

    @Test
    void reusingPipes() {
        Order order = newOrder(1004, "United States");

        PipelineInterface<Order> createOrderPipe = new Pipeline<Order>()
                .pipe(new CreateOrder())
                .pipe(new GenerateInvoice());

        PipelineInterface<Order> mainPipeline = new Pipeline<Order>()
                .pipe(createOrderPipe) // Pipe re-use!
                .pipe(new ProcessPayment())
                .pipe(new SendInvoice())
                .pipe(new ExportOrder());

        mainPipeline.process(order);

        assertTrue(order.isProcessed());
        assertTrue(order.isPaid());
    }
}
