package pipes.builder;

import pipes.PipelineInterface;
import pipes.processors.ProcessorInterface;

import java.util.function.Function;

/**
 * Collects stages so a pipeline can be composed step by step (for example conditionally) and
 * built at any given time.
 *
 * <p>Pipelines are immutable; builders are not. Use a builder when stages are decided at
 * runtime or composed in several places.</p>
 *
 * @param <T> the payload type flowing through the pipeline
 */
public interface PipelineBuilderInterface<T> {

    /**
     * Add a stage.
     *
     * @param stage any stage: a {@link pipes.stages.StageInterface}, a lambda, a method
     *              reference, a {@link Function} or another pipeline
     * @return this builder, for chaining
     * @throws NullPointerException if {@code stage} is {@code null}
     */
    PipelineBuilderInterface<T> add(Function<? super T, ? extends T> stage);

    /**
     * Build a new pipeline with the default {@link pipes.processors.FingersCrossedProcessor}.
     *
     * @return a new pipeline containing the stages added so far
     */
    default PipelineInterface<T> build() {
        return build(null);
    }

    /**
     * Build a new pipeline that uses the given processor.
     *
     * @param processor the processor, or {@code null} for the default
     *                  {@link pipes.processors.FingersCrossedProcessor}
     * @return a new pipeline containing the stages added so far
     */
    PipelineInterface<T> build(ProcessorInterface<T> processor);
}
