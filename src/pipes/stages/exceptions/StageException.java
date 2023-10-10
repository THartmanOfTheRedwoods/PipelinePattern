package pipes.stages.exceptions;

public class StageException extends RuntimeException {
    public StageException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
