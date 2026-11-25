package article;

import pipes.stages.StageInterface;

public class DoStage1 implements StageInterface<Payload> {
    @Override
    public Payload apply(Payload payload) {
        payload.setResult(payload.getResult() + "all");
        return payload;
    }
}
