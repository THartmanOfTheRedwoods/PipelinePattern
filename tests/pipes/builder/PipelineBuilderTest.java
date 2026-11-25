package pipes.builder;

import org.junit.jupiter.api.Test;
import pipes.PipelineInterface;
import pipes.processors.InterruptibleProcessor;
import stages.AddOneStage;
import stages.TimesTwoStage;

import static org.junit.jupiter.api.Assertions.*;

class PipelineBuilderTest {

    @Test
    void itBuildsAPipelineWithTheDefaultProcessor() {
        PipelineInterface<Integer> pipeline = new PipelineBuilder<Integer>()
                .add(new TimesTwoStage())
                .add(new AddOneStage())
                .build();

        assertEquals(21, pipeline.process(10));
    }

    @Test
    void itBuildsAPipelineWithAGivenProcessor() {
        PipelineInterface<Integer> pipeline = new PipelineBuilder<Integer>()
                .add(p -> p * 2)
                .add(p -> p + 1)
                .build(new InterruptibleProcessor<>(p -> p < 3));

        assertEquals(4, pipeline.process(2));
    }

    @Test
    void itSupportsConditionalStages() {
        for (boolean addStage : new boolean[]{true, false}) {
            PipelineBuilderInterface<Integer> builder = new PipelineBuilder<Integer>().add(new TimesTwoStage());
            if (addStage) {
                builder.add(new AddOneStage());
            }
            assertEquals(addStage ? 21 : 20, builder.build().process(10));
        }
    }

    @Test
    void builtPipelinesAreSnapshotsOfTheBuilder() {
        PipelineBuilder<Integer> builder = new PipelineBuilder<Integer>().add(new TimesTwoStage());
        PipelineInterface<Integer> first = builder.build();

        builder.add(new AddOneStage());
        PipelineInterface<Integer> second = builder.build();

        assertEquals(20, first.process(10), "adding stages must not change an already built pipeline");
        assertEquals(21, second.process(10));
    }

    @Test
    void anEmptyBuilderBuildsAPassThroughPipeline() {
        assertEquals("x", new PipelineBuilder<String>().build().process("x"));
    }

    @Test
    void itRejectsNullStages() {
        assertThrows(NullPointerException.class, () -> new PipelineBuilder<Integer>().add(null));
    }
}
