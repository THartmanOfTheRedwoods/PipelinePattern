package pipes.builder;

import pipes.Pipeline;
import pipes.PipelineInterface;
import pipes.processors.ProcessorInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * The default {@link PipelineBuilderInterface} implementation.
 *
 * <pre>{@code
 * PipelineBuilder<Order> builder = new PipelineBuilder<Order>()
 *         .add(new CreateOrder());
 *
 * if (order.getOrigin().equals("New Zealand")) {
 *     builder.add(new PreBookCarrier());
 * }
 *
 * PipelineInterface<Order> pipeline = builder.add(new ProcessPayment()).build();
 * }</pre>
 *
 * <p>Each call to {@code build} takes a snapshot of the stages: adding more stages afterwards
 * never changes a pipeline that has already been built.</p>
 *
 * @param <T> the payload type flowing through the pipeline
 */
public class PipelineBuilder<T> implements PipelineBuilderInterface<T> {

    private final List<Function<? super T, ? extends T>> stages = new ArrayList<>();

    /** Creates an empty builder. */
    public PipelineBuilder() {
    }

    @Override
    public PipelineBuilder<T> add(Function<? super T, ? extends T> stage) {
        this.stages.add(Objects.requireNonNull(stage, "stage must not be null"));
        return this;
    }

    @Override
    public PipelineInterface<T> build() {
        return build(null);
    }

    @Override
    public PipelineInterface<T> build(ProcessorInterface<T> processor) {
        return new Pipeline<>(processor, this.stages);
    }
}
