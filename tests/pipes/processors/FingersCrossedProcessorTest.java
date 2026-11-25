package pipes.processors;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FingersCrossedProcessorTest {

    // Ported from League\Pipeline's FingersCrossedProcessorTest
    @Test
    void itCanProcessStages() {
        FingersCrossedProcessor<Integer> processor = new FingersCrossedProcessor<>();

        List<Function<Integer, Integer>> stages = List.of(
                payload -> payload * 2,
                payload -> payload + 1);

        assertEquals(5, processor.process(2, stages));
    }

    @Test
    void itReturnsThePayloadWhenThereAreNoStages() {
        assertEquals("same", new FingersCrossedProcessor<String>().process("same", List.of()));
    }
}
