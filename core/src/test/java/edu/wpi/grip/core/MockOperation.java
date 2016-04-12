package edu.wpi.grip.core;


import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;

public class MockOperation implements Operation {

    @Override
    public OperationDescription getDescription() {
        return OperationDescription.builder()
                .name("Mock Operation")
                .description("A mock operation description")
                .build();
    }

    @Override
    public InputSocket<?>[] getInputSockets() {
        return new InputSocket<?>[0];
    }

    @Override
    public OutputSocket<?>[] getOutputSockets() {
        return new OutputSocket<?>[0];
    }

    @Override
    public void perform() {

    }
}
