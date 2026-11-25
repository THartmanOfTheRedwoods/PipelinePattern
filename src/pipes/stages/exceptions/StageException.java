package pipes.stages.exceptions;

/**
 * An optional, unchecked exception a stage can throw to short-circuit a pipeline.
 *
 * <p>Like League\Pipeline, this library is completely transparent when it comes to exceptions:
 * it never catches or silences anything. Any {@link RuntimeException} thrown by a stage stops
 * processing and propagates to the caller of {@code process(...)}. {@code StageException} is
 * simply a convenient, descriptive type to throw and catch.</p>
 */
public class StageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message.
     *
     * @param message the detail message
     */
    public StageException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and a cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public StageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception that wraps a cause.
     *
     * @param cause the underlying cause
     */
    public StageException(Throwable cause) {
        super(cause);
    }
}
