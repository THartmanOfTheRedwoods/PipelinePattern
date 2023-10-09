package pipes.processors;

import pipes.payloads.Payload;
import pipes.stages.StageInterface;

import java.util.List;

public interface ProcessorInterface {
    Payload process(Payload payload, List<StageInterface> stages);
}
