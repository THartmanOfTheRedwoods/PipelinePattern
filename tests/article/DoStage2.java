package article;

import pipes.stages.StageInterface;

public class DoStage2 implements StageInterface<Payload> {
    @Override
    public Payload apply(Payload payload) {
        payload.setResult(payload.getResult() + "the");
        return payload;
    }
}
