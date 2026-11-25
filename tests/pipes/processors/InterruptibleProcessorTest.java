package pipes.processors;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterruptibleProcessorTest {

    // Ported from League\Pipeline's InterruptibleProcessorTest
    @Test
    void itCanProcessStages() {
        InterruptibleProcessor<Integer> processor = new InterruptibleProcessor<>(payload -> payload < 3);

        List<Function<Integer, Integer>> stages = List.of(
                payload -> payload * 2,
                payload -> payload + 1);

        assertEquals(4, processor.process(2, stages));
    }

    @Test
    void itRunsEveryStageWhileTheCheckPasses() {
        InterruptibleProcessor<Integer> processor = new InterruptibleProcessor<>(payload -> true);

        List<Function<Integer, Integer>> stages = List.of(
                payload -> payload * 2,
                payload -> payload + 1);

        assertEquals(5, processor.process(2, stages));
    }

    @Test
    void itRequiresACheck() {
        assertThrows(NullPointerException.class, () -> new InterruptibleProcessor<Integer>(null));
    }
}
