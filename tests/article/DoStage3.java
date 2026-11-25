package article;

import pipes.stages.StageInterface;

public class DoStage3 implements StageInterface<Payload> {
    @Override
    public Payload apply(Payload payload) {
        payload.setResult(payload.getResult() + "things");
        return payload;
    }
}
