package pipes.builder;

import java.util.ArrayList;
import java.util.List;

import pipes.PipelineInterface;
import pipes.Pipeline;
import pipes.processors.ProcessorInterface;
import pipes.stages.StageInterface;

public class PipelineBuilder implements PipelineBuilderInterface {

    private List<StageInterface> stages = new ArrayList<>();
    @Override
    public PipelineBuilderInterface add(StageInterface stage) {
        this.stages.add(stage);
        return this;
    }

    @Override
    public PipelineInterface build(ProcessorInterface processor) {
        return new Pipeline(processor, this.stages);
    }
}
