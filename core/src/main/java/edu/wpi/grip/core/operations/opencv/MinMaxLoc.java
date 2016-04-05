package edu.wpi.grip.core.operations.opencv;

import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;

/**
 * Operation to call {@link opencv_core#minMaxLoc}
 */
public class MinMaxLoc implements CVOperation<MinMaxLoc> {

    public static final OperationDescription<MinMaxLoc> DESCRIPTION =
            CVOperation.defaultBuilder(MinMaxLoc.class)
                    .constructor(MinMaxLoc::new)
                    .name("Find Min and Max")
                    .description("Find the global minimum and manimum in a single channel grayscale image.")
                    .build();

    private final SocketHint<Mat>
            srcInputHint = SocketHints.Inputs.createMatSocketHint("Image", false),
            maskInputHint = SocketHints.Inputs.createMatSocketHint("Mask", true);

    private final SocketHint<Number>
            minValOutputHint = SocketHints.Outputs.createNumberSocketHint("Min Val", 0),
            maxValOutputHint = SocketHints.Outputs.createNumberSocketHint("Max Val", 0);

    private final SocketHint<Point>
            minLocOutputHint = SocketHints.Outputs.createPointSocketHint("Min Loc"),
            maxLocOutputHint = SocketHints.Outputs.createPointSocketHint("Max Loc");

    private final InputSocket<Mat> srcSocket;
    private final InputSocket<Mat> maskSocket;

    private final OutputSocket<Number> minValSocket;
    private final OutputSocket<Number> maxValSocket;
    private final OutputSocket<Point> minLocSocket;
    private final OutputSocket<Point> maxLocSocket;

    public MinMaxLoc(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.srcSocket = inputSocketFactory.create(srcInputHint);
        this.maskSocket = inputSocketFactory.create(maskInputHint);

        this.minValSocket = outputSocketFactory.create(minValOutputHint);
        this.maxValSocket = outputSocketFactory.create(maxValOutputHint);
        this.minLocSocket = outputSocketFactory.create(minLocOutputHint);
        this.maxLocSocket = outputSocketFactory.create(maxLocOutputHint);
    }

    @Override
    public OperationDescription<MinMaxLoc> getDescription() {
        return DESCRIPTION;
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        return new InputSocket[]{
                srcSocket,
                maskSocket
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets() {
        return new OutputSocket[]{
                minValSocket,
                maxValSocket,
                minLocSocket,
                maxLocSocket
        };
    }

    @Override
    public void perform() {
        final Mat src = srcSocket.getValue().get();
        Mat mask = maskSocket.getValue().get();
        if (mask.empty()) mask = null;
        final double minVal[] = new double[1];
        final double maxVal[] = new double[1];
        final Point minLoc = minLocSocket.getValue().get();
        final Point maxLoc = maxLocSocket.getValue().get();

        opencv_core.minMaxLoc(src, minVal, maxVal, minLoc, maxLoc, mask);
        minValSocket.setValue(minVal[0]);
        maxValSocket.setValue(maxVal[0]);
        minLocSocket.setValue(minLocSocket.getValue().get());
        maxLocSocket.setValue(maxLocSocket.getValue().get());
    }
}