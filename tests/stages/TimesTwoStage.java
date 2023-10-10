package stages;

import pipes.payloads.Payload;
import pipes.stages.StageInterface;

public class TimesTwoStage implements StageInterface {
    @Override
    public Payload apply(Payload payload) {
        int v = payload.getResult(Integer.class);
        payload.setResult(Integer.class, v*2);
        return payload;
    }
}
