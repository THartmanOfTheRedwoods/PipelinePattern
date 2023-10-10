package pipes.processors;

import pipes.payloads.Payload;
import pipes.stages.StageInterface;

import java.util.List;

// TODO: Implement a MementoProcessor for State, Rollback, and Error recovery
public interface ProcessorInterface {
    Payload process(Payload payload, List<StageInterface> stages);
}
