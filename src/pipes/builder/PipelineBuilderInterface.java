package pipes.builder;

import pipes.PipelineInterface;
import pipes.stages.StageInterface;
import pipes.processors.ProcessorInterface;

public interface PipelineBuilderInterface {
    public PipelineBuilderInterface add(StageInterface stage);
    public PipelineInterface build(ProcessorInterface processor);
}
