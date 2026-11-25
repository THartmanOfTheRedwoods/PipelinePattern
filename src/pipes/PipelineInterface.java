package pipes;

import pipes.stages.StageInterface;

import java.util.function.Function;

/**
 * An immutable chain of stages.
 *
 * <p>Because {@code PipelineInterface} extends {@link StageInterface}, a pipeline can be used as
 * a stage inside another pipeline.</p>
 *
 * @param <T> the payload type flowing through the pipeline
 */
public interface PipelineInterface<T> extends StageInterface<T> {

    /**
     * Create a <strong>new</strong> pipeline with an appended stage. The pipeline this method is
     * called on is left untouched.
     *
     * @param stage any stage: a {@link StageInterface}, a lambda, a method reference, a
     *              {@link Function} or another pipeline
     * @return a new pipeline containing the existing stages followed by {@code stage}
     * @throws NullPointerException if {@code stage} is {@code null}
     */
    PipelineInterface<T> pipe(Function<? super T, ? extends T> stage);

    /**
     * Process the payload by passing it through every stage.
     *
     * @param payload the initial payload
     * @return the payload returned by the last stage (or {@code payload} itself when there are
     *         no stages)
     */
    T process(T payload);

    /**
     * Invoking a pipeline as a stage processes the payload. This is the Java equivalent of
     * League\Pipeline's {@code __invoke}.
     */
    @Override
    default T apply(T payload) {
        return process(payload);
    }
}
