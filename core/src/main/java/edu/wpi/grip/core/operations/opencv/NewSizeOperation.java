package edu.wpi.grip.core.operations.opencv;


import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icons;
import org.bytedeco.javacpp.opencv_core.Size;

public class NewSizeOperation implements CVOperation {

    public static final OperationDescription DESCRIPTION =
            CVOperation.defaultBuilder()
                    .name("New Size")
                    .description("Create a size.")
                    .icon(Icons.iconStream("size"))
                    .build();

    private final SocketHint<Number> widthHint = SocketHints.Inputs.createNumberSpinnerSocketHint("width", -1, -1, Integer.MAX_VALUE);
    private final SocketHint<Number> heightHint = SocketHints.Inputs.createNumberSpinnerSocketHint("height", -1, -1, Integer.MAX_VALUE);
    private final SocketHint<Size> outputHint = SocketHints.Outputs.createSizeSocketHint("size");


    private final InputSocket<Number> widthSocket;
    private final InputSocket<Number> heightSocket;

    private final OutputSocket<Size> outputSocket;

    public NewSizeOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.widthSocket = inputSocketFactory.create(widthHint);
        this.heightSocket = inputSocketFactory.create(heightHint);

        this.outputSocket = outputSocketFactory.create(outputHint);
    }

    @Override
    public OperationDescription getDescription() {
        return DESCRIPTION;
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        return new InputSocket[]{
                widthSocket,
                heightSocket
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets() {
        return new OutputSocket[]{
                outputSocket
        };
    }

    @Override
    public void perform() {
        final int widthValue = widthSocket.getValue().get().intValue();
        final int heightValue = heightSocket.getValue().get().intValue();
        outputSocket.setValue(new Size(widthValue, heightValue));
    }
}

