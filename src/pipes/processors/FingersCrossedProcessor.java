package pipes.processors;

import java.util.List;
import java.util.function.Function;

/**
 * Invokes every stage in order, passing the result of one stage to the next, and keeps its
 * fingers crossed that nothing goes wrong. This is the default processor.
 *
 * @param <T> the payload type flowing through the pipeline
 */
public class FingersCrossedProcessor<T> implements ProcessorInterface<T> {

    /** Creates a processor that runs every stage. */
    public FingersCrossedProcessor() {
    }

    @Override
    public T process(T payload, List<? extends Function<? super T, ? extends T>> stages) {
        for (Function<? super T, ? extends T> stage : stages) {
            payload = stage.apply(payload);
        }
        return payload;
    }
}
