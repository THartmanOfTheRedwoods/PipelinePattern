package pipes.builder;

import pipes.PipelineInterface;
import pipes.stages.StageInterface;
import pipes.processors.ProcessorInterface;

public interface PipelineBuilderInterface {
    PipelineBuilderInterface add(StageInterface stage);
    PipelineInterface build(ProcessorInterface processor);
}
