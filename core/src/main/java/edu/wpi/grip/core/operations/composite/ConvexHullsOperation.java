package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;

import static org.bytedeco.javacpp.opencv_core.MatVector;
import static org.bytedeco.javacpp.opencv_imgproc.convexHull;

/**
 * An {@link Operation} that finds the convex hull of each of a list of contours.
 * <p>
 * This can help remove holes in detected shapes, making them easier to analyze.
 */
public class ConvexHullsOperation implements Operation<ConvexHullsOperation> {

    public static final OperationDescription<ConvexHullsOperation> DESCRIPTION =
            OperationDescription.builder(ConvexHullsOperation.class)
                    .constructor(ConvexHullsOperation::new)
                    .name("Convex Hulls")
                    .description("Compute the convex hulls of contours")
                    .category(OperationDescription.Category.FEATURE_DETECTION)
                    .build();

    private final SocketHint<ContoursReport> contoursHint = new SocketHint.Builder<>(ContoursReport.class)
            .identifier("Contours").initialValueSupplier(ContoursReport::new).build();

    private final InputSocket<ContoursReport> inputSocket;
    private final OutputSocket<ContoursReport> outputSocket;

    public ConvexHullsOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.inputSocket = inputSocketFactory.create(contoursHint);

        this.outputSocket = outputSocketFactory.create(contoursHint);
    }

    @Override
    public OperationDescription<ConvexHullsOperation> getDescription() {
        return DESCRIPTION;
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        return new InputSocket<?>[]{
                inputSocket
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets() {
        return new OutputSocket<?>[]{
                outputSocket
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public void perform() {
        final MatVector inputContours = inputSocket.getValue().get().getContours();
        final MatVector outputContours = new MatVector(inputContours.size());

        for (int i = 0; i < inputContours.size(); i++) {
            convexHull(inputContours.get(i), outputContours.get(i));
        }

        outputSocket.setValue(new ContoursReport(outputContours,
                inputSocket.getValue().get().getRows(), inputSocket.getValue().get().getCols()));
    }
}
