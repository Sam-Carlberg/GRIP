package edu.wpi.grip.core.operations.composite;


import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.LinkedSocketHint;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.sockets.SocketsProvider;

import java.util.Optional;

public class ValveOperation implements Operation {

    public static OperationDescription DESCRIPTION =
            OperationDescription.builder()
                    .name("Valve")
                    .description("Toggle an output socket on or off using a boolean")
                    .category(OperationDescription.Category.LOGICAL)
                    .build();

    // This hint toggles the switch between using the true and false sockets
    private final SocketHint<Boolean> switcherHint = SocketHints.createBooleanSocketHint("valve", true);
    private final LinkedSocketHint linkedSocketHint;

    private final InputSocket<Boolean> switcherSocket;
    private final InputSocket inputSocket; // Intentionally using raw types

    private final OutputSocket outputSocket;

    public ValveOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.linkedSocketHint = new LinkedSocketHint(inputSocketFactory, outputSocketFactory);

        this.switcherSocket = inputSocketFactory.create(switcherHint);
        this.inputSocket = linkedSocketHint.linkedInputSocket("Input");

        this.outputSocket = linkedSocketHint.linkedOutputSocket("Output");
    }

    @Override
    public OperationDescription getDescription() {
        return DESCRIPTION;
    }

    @Override
    public SocketsProvider getSockets() {
        final InputSocket<?>[] inputs = new InputSocket[]{
                switcherSocket,
                inputSocket
        };
        final OutputSocket<?>[] outputs = new OutputSocket[]{
                outputSocket
        };
        return new SocketsProvider(inputs, outputs);
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        throw new UnsupportedOperationException("This method should not be used");
    }

    @Override
    public OutputSocket<?>[] createOutputSockets() {
        throw new UnsupportedOperationException("This method should not be used");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void perform() {
        // If the input is true pass the value through
        if (switcherSocket.getValue().get()) {
            outputSocket.setValueOptional(inputSocket.getValue());
        } else {
            outputSocket.setValueOptional(Optional.empty());
        }
    }
}
