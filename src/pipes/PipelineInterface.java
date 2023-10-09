package pipes;

import pipes.payloads.Payload;
import pipes.stages.StageInterface;

public interface PipelineInterface extends StageInterface {
    PipelineInterface pipe(StageInterface stage);
    Payload process(Payload payload);
}
