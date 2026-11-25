package pipes.stages;

import java.util.function.UnaryOperator;

/**
 * A single operation in a pipeline.
 *
 * <p>This is the Java counterpart of League\Pipeline's {@code StageInterface}. In PHP a stage is
 * anything {@code callable}; in Java a stage is anything that can act as a
 * {@link java.util.function.Function Function&lt;T, T&gt;}. Because this interface is a
 * {@link FunctionalInterface}, a stage can be:</p>
 *
 * <ul>
 *     <li>a class that implements {@code StageInterface<T>} (class based stages),</li>
 *     <li>a lambda, e.g. {@code payload -> payload * 2},</li>
 *     <li>a method reference, e.g. {@code String::trim},</li>
 *     <li>another {@link pipes.PipelineInterface pipeline} (pipelines are stages too).</li>
 * </ul>
 *
 * <p>A stage receives the current payload and returns the payload for the next stage. It may
 * act on, manipulate, decorate or completely replace the payload.</p>
 *
 * @param <T> the payload type flowing through the pipeline
 */
@FunctionalInterface
public interface StageInterface<T> extends UnaryOperator<T> {

    /**
     * Process the payload.
     *
     * @param payload the payload produced by the previous stage (or the initial payload)
     * @return the payload handed to the next stage
     */
    @Override
    T apply(T payload);
}
