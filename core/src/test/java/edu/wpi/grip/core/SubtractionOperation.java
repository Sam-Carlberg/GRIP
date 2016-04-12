package edu.wpi.grip.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;

public class SubtractionOperation implements Operation {
    private SocketHint<Number>
            aHint = SocketHints.createNumberSocketHint("a", 0.0),
            bHint = SocketHints.createNumberSocketHint("b", 0.0),
            cHint = SocketHints.Outputs.createNumberSocketHint("c", 0.0);

    private InputSocket<Number> a, b;
    private OutputSocket<Number> c;

    public SubtractionOperation() {
        Injector injector = Guice.createInjector(new GRIPCoreModule());
        InputSocket.Factory isf = injector.getInstance(InputSocket.Factory.class);
        OutputSocket.Factory osf = injector.getInstance(OutputSocket.Factory.class);

        a = isf.create(aHint);
        b = isf.create(bHint);
        c = osf.create(cHint);
    }

    @Override
    public OperationDescription getDescription() {
        return OperationDescription.builder()
                .name("Subtract")
                .description("Computer the difference between two doubles")
                .build();
    }

    @Override
    public InputSocket[] getInputSockets() {
        return new InputSocket[]{
                a, b
        };
    }

    @Override
    public OutputSocket[] getOutputSockets() {
        return new OutputSocket[]{
                c
        };
    }

    @Override
    public void perform() {
        c.setValue(a.getValue().get().doubleValue() - b.getValue().get().doubleValue());
    }
}
