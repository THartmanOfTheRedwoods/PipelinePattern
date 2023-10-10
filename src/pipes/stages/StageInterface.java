package pipes.stages;

import pipes.payloads.Payload;
import pipes.stages.exceptions.StageException;

public interface StageInterface {
    // TODO: Turn stages into closures or callbacks so we can pretend they are callables like in PHP and Python.
    Payload apply(Payload payload) throws StageException;
}
