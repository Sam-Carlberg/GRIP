package edu.wpi.grip.core.operations.opencv;


import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icons;
import org.bytedeco.javacpp.opencv_core.Point;

public class NewPointOperation implements CVOperation<NewPointOperation> {

    public static final OperationDescription<NewPointOperation> DESCRIPTION =
            CVOperation.defaultBuilder(NewPointOperation.class)
                    .constructor(NewPointOperation::new)
                    .name("New Point")
                    .description("Create a point by (x,y) value.")
                    .icon(Icons.iconStream("point"))
                    .build();

    private final SocketHint<Number> xHint = SocketHints.Inputs.createNumberSpinnerSocketHint("x", -1,
            Integer.MIN_VALUE, Integer.MAX_VALUE);
    private final SocketHint<Number> yHint = SocketHints.Inputs.createNumberSpinnerSocketHint("y", -1,
            Integer.MIN_VALUE, Integer.MAX_VALUE);
    private final SocketHint<Point> outputHint = SocketHints.Outputs.createPointSocketHint("point");


    private final InputSocket<Number> xSocket;
    private final InputSocket<Number> ySocket;

    private final OutputSocket<Point> outputSocket;

    public NewPointOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.xSocket = inputSocketFactory.create(xHint);
        this.ySocket = inputSocketFactory.create(yHint);

        this.outputSocket = outputSocketFactory.create(outputHint);
    }

    @Override
    public OperationDescription<NewPointOperation> getDescription() {
        return DESCRIPTION;
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        return new InputSocket[]{
                xSocket,
                ySocket
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
        final int xValue = xSocket.getValue().get().intValue();
        final int yValue = ySocket.getValue().get().intValue();
        outputSocket.setValue(new Point(xValue, yValue));
    }
}
