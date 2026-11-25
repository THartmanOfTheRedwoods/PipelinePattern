package pipes;

import org.junit.jupiter.api.Test;
import pipes.processors.InterruptibleProcessor;
import stages.AddOneStage;
import stages.TimesTwoStage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

    // ---- Ported from League\Pipeline's PipelineTest ----

    @Test
    void itPassesThroughInformationWithoutStages() {
        Pipeline<Integer> pipeline = new Pipeline<>();

        assertEquals(10, pipeline.process(10));
    }

    @Test
    void itUsesStagesToProcessThePipeline() {
        PipelineInterface<Integer> pipeline = new Pipeline<Integer>()
                .pipe(p -> p * 10)
                .pipe(p -> p - 10);

        assertEquals(90, pipeline.process(10));
    }

    @Test
    void itCanBeComposedByPipingMultiplePipelines() {
        Pipeline<Integer> pipeline1 = new Pipeline<>(null, p -> p * 10);
        Pipeline<Integer> pipeline2 = new Pipeline<>(null, p -> p - 10);

        PipelineInterface<Integer> pipeline = new Pipeline<Integer>().pipe(pipeline1).pipe(pipeline2);

        assertEquals(90, pipeline.apply(10));  // apply() is Java's __invoke()
    }

    // ---- League\Pipeline README examples ----

    @Test
    void classBasedStagesFromTheReadme() {
        PipelineInterface<Integer> pipeline = new Pipeline<Integer>()
                .pipe(new TimesTwoStage())
                .pipe(new AddOneStage());

        assertEquals(21, pipeline.process(10));
    }

    @Test
    void exceptionsAreNeverCaughtOrSilencedByThePipeline() {
        PipelineInterface<Object> pipeline = new Pipeline<>().pipe(p -> {
            throw new IllegalStateException("boom");
        });

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> pipeline.process("payload"));
        assertEquals("boom", e.getMessage());
    }

    // ---- Immutability ----

    @Test
    void pipeReturnsANewPipelineAndLeavesTheOriginalUntouched() {
        Pipeline<Integer> original = new Pipeline<Integer>().pipe(p -> p * 2);
        Pipeline<Integer> extended = original.pipe(p -> p + 1);

        assertNotSame(original, extended);
        assertEquals(20, original.process(10));
        assertEquals(21, extended.process(10));
    }

    @Test
    void laterChangesToTheConstructorListDoNotLeakIntoThePipeline() {
        List<Function<? super Integer, ? extends Integer>> stages = new ArrayList<>();
        stages.add(p -> p * 2);
        Pipeline<Integer> pipeline = new Pipeline<>(null, stages);

        stages.add(p -> p + 1000);

        assertEquals(20, pipeline.process(10));
    }

    // ---- Java specific behaviour ----

    @Test
    void stagesCanReplaceThePayload() {
        record Box(int value) { }

        Box result = new Pipeline<Box>()
                .pipe(b -> new Box(b.value() + 1))
                .process(new Box(1));

        assertEquals(new Box(2), result);
    }

    @Test
    void acceptsMethodReferencesAndPlainFunctions() {
        Function<String, String> exclaim = s -> s + "!";

        String result = new Pipeline<String>()
                .pipe(String::trim)
                .pipe(exclaim)
                .process("  hi  ");

        assertEquals("hi!", result);
    }

    @Test
    void acceptsStagesWrittenForASuperType() {
        Function<Object, Integer> length = o -> o.toString().length();  // Function<? super Integer, ? extends Integer>

        assertEquals(5, new Pipeline<Integer>().pipe(length).process(12345));
    }

    @Test
    void usesTheGivenProcessor() {
        Pipeline<Integer> pipeline = new Pipeline<>(new InterruptibleProcessor<>(p -> p < 3),
                p -> p * 2,
                p -> p + 1);

        assertEquals(4, pipeline.process(2));
        assertEquals(4, pipeline.pipe(p -> p * 100).process(2), "piping keeps the processor");
    }

    @Test
    void nullProcessorFallsBackToFingersCrossed() {
        assertEquals(3, new Pipeline<Integer>(null, p -> p + 1, p -> p + 1).process(1));
    }

    @Test
    void rejectsNullStages() {
        assertThrows(NullPointerException.class, () -> new Pipeline<Integer>().pipe(null));
    }
}
