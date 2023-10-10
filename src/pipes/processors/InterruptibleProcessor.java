package pipes.processors;

import pipes.payloads.Payload;
import pipes.stages.StageInterface;

import java.util.List;
import java.util.function.Predicate;

public class InterruptibleProcessor implements ProcessorInterface {

    private final Predicate<Payload> check;
    public InterruptibleProcessor(Predicate<Payload> check) {
        this.check = check;
    }
    @Override
    public Payload process(Payload payload, List<StageInterface> stages) {
        for(StageInterface stage : stages) {
            payload = stage.apply(payload);

            if(!check.test(payload)) {  // This allows us to stop processing if the Predicate test is not true
                return payload;
            }
        }
        return payload;
    }
}
