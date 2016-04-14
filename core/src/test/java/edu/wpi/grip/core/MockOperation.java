package edu.wpi.grip.core;


import com.google.common.collect.ImmutableList;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;

import java.util.List;

public class MockOperation implements Operation {

    @Override
    public OperationDescription getDescription() {
        return OperationDescription.builder()
                .name("Mock Operation")
                .description("A mock operation description")
                .build();
    }

    @Override
    public List<InputSocket> getInputSockets() {
        return ImmutableList.of();
    }

    @Override
    public List<OutputSocket> getOutputSockets() {
        return ImmutableList.of();
    }

    @Override
    public void perform() {

    }
}
