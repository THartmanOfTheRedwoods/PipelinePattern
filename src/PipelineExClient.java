import pipes.payloads.Payload;
import pipes.PipelineInterface;
import pipes.builder.PipelineBuilder;
import pipes.builder.PipelineBuilderInterface;
import pipes.processors.FingersCrossedProcessor;
import pipes.stages.AddOneStage;
import pipes.stages.TimesTwoStage;

public class PipelineExClient {
    public static void main(String[] args) {
        PipelineBuilderInterface pb = new PipelineBuilder();
        pb.add(new TimesTwoStage())
                .add(new AddOneStage());
        PipelineInterface pipeline = pb.build(new FingersCrossedProcessor());
        Payload p = new Payload(Integer.class, 32);
        pipeline.process(p);

        System.out.println(p.getResult(Integer.class));
    }
}
