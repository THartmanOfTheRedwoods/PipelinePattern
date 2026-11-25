package pipes;

import pipes.processors.FingersCrossedProcessor;
import pipes.processors.ProcessorInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * The default, immutable pipeline implementation.
 *
 * <pre>{@code
 * PipelineInterface<Integer> pipeline = new Pipeline<Integer>()
 *         .pipe(new TimesTwoStage())
 *         .pipe(payload -> payload + 1);
 *
 * pipeline.process(10); // 21
 * }</pre>
 *
 * <p>Every call to {@link #pipe(Function)} returns a new pipeline, so a pipeline can be shared,
 * reused and extended without side effects. A pipeline is thread-safe as long as its stages
 * are.</p>
 *
 * @param <T> the payload type flowing through the pipeline
 */
public class Pipeline<T> implements PipelineInterface<T> {

    private final ProcessorInterface<T> processor;
    private final List<Function<? super T, ? extends T>> stages;

    /**
     * An empty pipeline that uses the {@link FingersCrossedProcessor}.
     */
    public Pipeline() {
        this(null, Collections.emptyList());
    }

    /**
     * A pipeline with a processor and an initial set of stages.
     *
     * @param processor the processor that invokes the stages, or {@code null} for the default
     *                  {@link FingersCrossedProcessor}
     * @param stages    the initial stages, in execution order
     * @throws NullPointerException if any stage is {@code null}
     */
    @SafeVarargs
    public Pipeline(ProcessorInterface<T> processor, Function<? super T, ? extends T>... stages) {
        List<Function<? super T, ? extends T>> list = new ArrayList<>(stages.length);
        for (Function<? super T, ? extends T> stage : stages) {
            list.add(stage);
        }
        this.processor = processor != null ? processor : new FingersCrossedProcessor<>();
        this.stages = List.copyOf(list);
    }

    /**
     * A pipeline with a processor and an initial list of stages. The list is copied, so later
     * changes to it do not affect this pipeline.
     *
     * @param processor the processor that invokes the stages, or {@code null} for the default
     *                  {@link FingersCrossedProcessor}
     * @param stages    the initial stages, in execution order
     * @throws NullPointerException if {@code stages} is, or contains, {@code null}
     */
    public Pipeline(ProcessorInterface<T> processor, List<? extends Function<? super T, ? extends T>> stages) {
        this.processor = processor != null ? processor : new FingersCrossedProcessor<>();
        this.stages = List.copyOf(stages);
    }

    @Override
    public Pipeline<T> pipe(Function<? super T, ? extends T> stage) {
        Objects.requireNonNull(stage, "stage must not be null");
        List<Function<? super T, ? extends T>> next = new ArrayList<>(this.stages.size() + 1);
        next.addAll(this.stages);
        next.add(stage);
        return new Pipeline<>(this.processor, next);
    }

    @Override
    public T process(T payload) {
        return this.processor.process(payload, this.stages);
    }
}
