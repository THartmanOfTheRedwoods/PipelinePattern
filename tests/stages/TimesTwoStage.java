package stages;

import pipes.stages.StageInterface;

public class TimesTwoStage implements StageInterface<Integer> {
    @Override
    public Integer apply(Integer payload) {
        return payload * 2;
    }
}
