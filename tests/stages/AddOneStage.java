package stages;

import pipes.stages.StageInterface;

public class AddOneStage implements StageInterface<Integer> {
    @Override
    public Integer apply(Integer payload) {
        return payload + 1;
    }
}
