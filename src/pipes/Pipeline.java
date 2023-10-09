package pipes;

import pipes.payloads.Payload;
import pipes.processors.ProcessorInterface;
import pipes.stages.StageInterface;

import java.util.List;

public class Pipeline implements PipelineInterface {

    private final List<StageInterface> stages;
    private final ProcessorInterface processor;

    public Pipeline(ProcessorInterface processor, List<StageInterface> stages) {
        this.processor = processor;
        this.stages = stages;
    }

    @Override
    public PipelineInterface pipe(StageInterface stage) {
        // TODO: Figure out best way to clone pipeline so we can treat them as immutable here.
        this.stages.add(stage);
        return this;
    }

    @Override
    public Payload process(Payload payload) {
        return this.processor.process(payload, this.stages);
    }

    @Override
    public Payload apply(Payload payload) {
        return this.process(payload);
    }
}
