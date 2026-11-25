package pipes.processors;

import java.util.List;
import java.util.function.Function;

/**
 * A processor decides <em>how</em> a pipeline's stages are invoked.
 *
 * <p>The pipeline only holds the stages; the processor is responsible for running them. This
 * makes it possible to change the execution strategy (run everything, stop early, ...) without
 * touching the stages or the pipeline.</p>
 *
 * @param <T> the payload type flowing through the pipeline
 */
// TODO: Implement a MementoProcessor for State, Rollback, and Error recovery
@FunctionalInterface
public interface ProcessorInterface<T> {

    /**
     * Process the payload using the given stages.
     *
     * @param payload the initial payload
     * @param stages  the stages to invoke, in order
     * @return the resulting payload
     */
    T process(T payload, List<? extends Function<? super T, ? extends T>> stages);
}
