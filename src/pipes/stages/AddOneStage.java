package pipes.stages;

import pipes.payloads.Payload;

public class AddOneStage implements StageInterface {
    @Override
    public Payload apply(Payload payload) {
        int v = payload.getResult(Integer.class);
        payload.setResult(Integer.class, v + 1);
        return payload;
    }
}
