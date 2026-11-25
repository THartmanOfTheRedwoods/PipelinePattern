package article;

import pipes.Pipeline;
import pipes.PipelineInterface;

public class RunAllTheThings {
    public Payload doIt() {
        PipelineInterface<Payload> pipeline = new Pipeline<Payload>()
                .pipe(new DoStage1())
                .pipe(new DoStage2())
                .pipe(new DoStage3());

        // The payload is an object that's passed between stages
        Payload payload = new Payload();

        // Run the pipeline and return the result
        return pipeline.process(payload);
    }
}
