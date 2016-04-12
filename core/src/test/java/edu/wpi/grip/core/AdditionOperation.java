package edu.wpi.grip.core;

import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;

public class AdditionOperation implements Operation {
    private SocketHint<Number>
            aHint = SocketHints.createNumberSocketHint("a", 0.0),
            bHint = SocketHints.createNumberSocketHint("b", 0.0),
            cHint = SocketHints.Outputs.createNumberSocketHint("c", 0.0);

    private InputSocket<Number> a, b;
    private OutputSocket<Number> c;

    public AdditionOperation(InputSocket.Factory isf, OutputSocket.Factory osf) {
        a = isf.create(aHint);
        b = isf.create(bHint);
        c = osf.create(cHint);
    }

    @Override
    public OperationDescription getDescription() {
        return OperationDescription.builder()
                .name("Add")
                .description("Compute the sum of two doubles")
                .build();
    }

    @Override
    public InputSocket[] createInputSockets() {
        return new InputSocket[]{a, b};
    }

    @Override
    public OutputSocket[] createOutputSockets() {
        return new OutputSocket[]{c};
    }

    @Override
    public void perform() {
        System.out.println("Performing addition");
        double val_a = a.getValue().get().doubleValue();
        double val_b = b.getValue().get().doubleValue();
        double val_c = val_a + val_b;
        System.out.printf("%s + %s = %s%n", val_a, val_b, val_c);
        c.setValue(val_c);
    }
}
