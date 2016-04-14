package edu.wpi.grip.core;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.util.MockExceptionWitness;

import java.util.Collections;

public class MockStep extends Step {

    public MockStep() {
        super(null, Collections.emptyList(), Collections.emptyList(), origin -> null);
    }

    public static Step createMockStepWithOperation() {
        final EventBus eventBus = new EventBus();
        return new Step.Factory(origin -> new MockExceptionWitness(eventBus, origin)).create(new MockOperation());
    }
}
