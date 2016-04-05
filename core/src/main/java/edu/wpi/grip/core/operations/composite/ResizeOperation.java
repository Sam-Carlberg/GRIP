package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icons;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_AREA;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_CUBIC;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_LANCZOS4;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_LINEAR;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_NEAREST;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

/**
 * Scale an image to an exact width and height using one of several interpolation modes.  Scaling images down can
 * be a useful optimization, and scaling them up might be necessary for combining multiple images that are different
 * sizes.
 */
public class ResizeOperation implements Operation<ResizeOperation> {

    public static final OperationDescription<ResizeOperation> DESCRIPTION =
            OperationDescription.builder(ResizeOperation.class)
                    .constructor(ResizeOperation::new)
                    .name("Resize Image")
                    .description("Scale an image to an exact size")
                    .category(OperationDescription.Category.IMAGE_PROCESSING)
                    .icon(Icons.iconStream("resize"))
                    .build();

    private final InputSocket<Mat> inputSocket;
    private final InputSocket<Number> widthSocket;
    private final InputSocket<Number> heightSocket;
    private final InputSocket<Interpolation> interpolationSocket;

    private final OutputSocket<Mat> outputSocket;

    public ResizeOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.inputSocket = inputSocketFactory.create(SocketHints.Inputs.createMatSocketHint("Input", false));
        this.widthSocket = inputSocketFactory.create(SocketHints.Inputs.createNumberSpinnerSocketHint("Width", 640));
        this.heightSocket = inputSocketFactory.create(SocketHints.Inputs.createNumberSpinnerSocketHint("Height", 480));
        this.interpolationSocket = inputSocketFactory.create(SocketHints.createEnumSocketHint("Interpolation", Interpolation.CUBIC));

        this.outputSocket = outputSocketFactory.create(SocketHints.Outputs.createMatSocketHint("Output"));
    }

    private enum Interpolation {
        NEAREST("None", INTER_NEAREST),
        LINEAR("Linear", INTER_LINEAR),
        CUBIC("Cubic", INTER_CUBIC),
        LANCZOS("Lanczos", INTER_LANCZOS4),
        AREA("Area", INTER_AREA);

        final String label;
        final int value;

        Interpolation(String label, int value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public OperationDescription<ResizeOperation> getDescription() {
        return DESCRIPTION;
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        return new InputSocket<?>[]{
                inputSocket,
                widthSocket,
                heightSocket,
                interpolationSocket
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets() {
        return new OutputSocket<?>[]{
                outputSocket
        };
    }

    @Override
    public void perform() {
        final Mat input = inputSocket.getValue().get();
        final Number width = widthSocket.getValue().get();
        final Number height = heightSocket.getValue().get();
        final Interpolation interpolation = interpolationSocket.getValue().get();

        final Mat output = outputSocket.getValue().get();

        resize(input, output, new Size(width.intValue(), height.intValue()), 0.0, 0.0, interpolation.value);

        outputSocket.setValue(output);
    }
}
