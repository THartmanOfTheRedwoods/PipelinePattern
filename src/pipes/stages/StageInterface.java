package pipes.stages;

import pipes.payloads.Payload;

public interface StageInterface {
    Payload apply(Payload payload);
}
