package pipes.processors;

import pipes.payloads.Payload;
import pipes.stages.StageInterface;

import java.util.List;

public class FingersCrossedProcessor implements ProcessorInterface {
    @Override
    public Payload process(Payload payload, List<StageInterface> stages) {
        for(StageInterface stage : stages) {
            payload = stage.apply(payload);
        }
        return payload;
    }
}
