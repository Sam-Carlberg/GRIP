package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icons;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * An {@link Operation} that converts a color image into a binary image based on threshold ranges for each channel
 */
public class RGBThresholdOperation extends ThresholdOperation<RGBThresholdOperation> {

    public static final OperationDescription<RGBThresholdOperation> DESCRIPTION =
            OperationDescription.builder(RGBThresholdOperation.class)
                    .constructor(RGBThresholdOperation::new)
                    .name("RGB Threshold")
                    .description("Segment an image based on color ranges")
                    .category(OperationDescription.Category.IMAGE_PROCESSING)
                    .icon(Icons.iconStream("threshold"))
                    .build();

    private static final Logger logger = Logger.getLogger(RGBThresholdOperation.class.getName());
    private final SocketHint<Mat> inputHint = SocketHints.Inputs.createMatSocketHint("Input", false);
    private final SocketHint<List<Number>> redHint = SocketHints.Inputs.createNumberListRangeSocketHint("Red", 0.0, 255.0);
    private final SocketHint<List<Number>> greenHint = SocketHints.Inputs.createNumberListRangeSocketHint("Green", 0.0, 255.0);
    private final SocketHint<List<Number>> blueHint = SocketHints.Inputs.createNumberListRangeSocketHint("Blue", 0.0, 255.0);

    private final SocketHint<Mat> outputHint = SocketHints.Outputs.createMatSocketHint("Output");


    private final InputSocket<Mat> inputSocket;
    private final InputSocket<List<Number>> redSocket;
    private final InputSocket<List<Number>> greenSocket;
    private final InputSocket<List<Number>> blueSocket;

    private final OutputSocket<Mat> outputSocket;

    public RGBThresholdOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.inputSocket = inputSocketFactory.create(inputHint);
        this.redSocket = inputSocketFactory.create(redHint);
        this.greenSocket = inputSocketFactory.create(greenHint);
        this.blueSocket = inputSocketFactory.create(blueHint);

        this.outputSocket = outputSocketFactory.create(outputHint);
    }

    @Override
    public OperationDescription<RGBThresholdOperation> getDescription() {
        return DESCRIPTION;
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        return new InputSocket<?>[]{
                inputSocket,
                redSocket,
                greenSocket,
                blueSocket
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets() {
        return new OutputSocket<?>[]{
                outputSocket
        };
    }

    @Override
    public Optional<Mat[]> createData() {
        return Optional.of(new Mat[]{new Mat(), new Mat()});
    }

    @Override
    public void perform(Optional<?> data) {
        final Mat[] dataArray = (Mat[]) data.orElseThrow(() -> new IllegalStateException("Data was not provided"));

        final Mat input = inputSocket.getValue().get();
        final List<Number> channel1 = redSocket.getValue().get();
        final List<Number> channel2 = greenSocket.getValue().get();
        final List<Number> channel3 = blueSocket.getValue().get();

        if (input.channels() != 3) {
            throw new IllegalArgumentException("RGB Threshold needs a 3-channel input");
        }

        final Mat output = outputSocket.getValue().get();


        final Scalar lowScalar = new Scalar(
                channel3.get(0).doubleValue(),
                channel2.get(0).doubleValue(),
                channel1.get(0).doubleValue(), 0);

        final Scalar highScalar = new Scalar(
                channel3.get(1).doubleValue(),
                channel2.get(1).doubleValue(),
                channel1.get(1).doubleValue(), 0);

        final Mat low = reallocateMatIfInputSizeOrWidthChanged(dataArray, 0, lowScalar, input);
        final Mat high = reallocateMatIfInputSizeOrWidthChanged(dataArray, 1, highScalar, input);

        try {
            inRange(input, low, high, output);

            outputSocket.setValue(output);
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
