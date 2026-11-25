package pipes.processors;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Invokes the stages in order, but checks the payload after every stage. As soon as the check
 * returns {@code false} processing stops and the current payload is returned.
 *
 * <pre>{@code
 * // Stop as soon as the payload is no longer below 3
 * new Pipeline<Integer>(new InterruptibleProcessor<>(payload -> payload < 3),
 *         payload -> payload * 2,
 *         payload -> payload + 1)
 *     .process(2); // 4: the second stage never runs
 * }</pre>
 *
 * @param <T> the payload type flowing through the pipeline
 */
public class InterruptibleProcessor<T> implements ProcessorInterface<T> {

    private final Predicate<? super T> check;

    /**
     * Creates a processor that stops as soon as {@code check} fails.
     *
     * @param check evaluated after each stage; processing continues only while it returns
     *              {@code true}
     */
    public InterruptibleProcessor(Predicate<? super T> check) {
        this.check = Objects.requireNonNull(check, "check must not be null");
    }

    @Override
    public T process(T payload, List<? extends Function<? super T, ? extends T>> stages) {
        for (Function<? super T, ? extends T> stage : stages) {
            payload = stage.apply(payload);

            if (!this.check.test(payload)) {  // Stop processing as soon as the check fails
                return payload;
            }
        }
        return payload;
    }
}
