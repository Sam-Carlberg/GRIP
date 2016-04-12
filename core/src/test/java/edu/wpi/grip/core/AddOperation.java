package edu.wpi.grip.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.grip.core.sockets.*;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;


/**
 * Performs the opencv add operation
 */
public class AddOperation implements Operation {
    private SocketHint<Mat>
            aHint = SocketHints.Inputs.createMatSocketHint("a", false),
            bHint = SocketHints.Inputs.createMatSocketHint("b", false),
            sumHint = SocketHints.Inputs.createMatSocketHint("sum", true);

    private InputSocket<Mat> a, b;
    private OutputSocket<Mat> sum;

    public AddOperation() {
        Injector injector = Guice.createInjector(new GRIPCoreModule());

        InputSocket.Factory isf = injector.getInstance(InputSocket.Factory.class);
        OutputSocket.Factory osf = injector.getInstance(OutputSocket.Factory.class);

        a = isf.create(aHint);
        b = isf.create(bHint);
        sum = osf.create(sumHint);
    }

    public OperationDescription getDescription() {
        return OperationDescription.builder()
                .name("OpenCV Add")
                .description("Compute the per-pixel sum of two images.")
                .build();
    }

    @Override
    public InputSocket[] createInputSockets() {
        return new InputSocket[]{
                a, b
        };
    }

    @Override
    public OutputSocket[] createOutputSockets() {
        return new OutputSocket[]{
                sum
        };
    }

    @Override
    public void perform() {
        opencv_core.add(a.getValue().get(), b.getValue().get(), sum.getValue().get());
    }
}
