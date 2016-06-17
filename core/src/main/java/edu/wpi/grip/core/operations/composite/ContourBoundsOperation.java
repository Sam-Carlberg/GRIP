package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.RotatedRect;

import static org.bytedeco.javacpp.opencv_imgproc.minAreaRect;

/**
 * An operation for finding the smallest bounding boxes for contours.
 */
public class ContourBoundsOperation implements Operation {

    public static final OperationDescription DESCRIPTION =
        OperationDescription.builder()
            .name("Calculate contour bounds")
            .summary("Calculates the smallest bounding boxes for contours")
            .category(OperationDescription.Category.FEATURE_DETECTION)
            .build();

    private final SocketHint<ContoursReport> contoursHint =
        new SocketHint.Builder<>(ContoursReport.class)
            .initialValue(new ContoursReport())
            .identifier("Contours")
            .build();

    @SuppressWarnings("unchecked")
    private final SocketHint<BoundingBoxReport<RotatedRect>> boundingBoxesHint =
        new SocketHint.Builder<>((Class<BoundingBoxReport<RotatedRect>>) new TypeToken<BoundingBoxReport<RotatedRect>>() {}.getRawType())
            .initialValue(BoundingBoxReport.emptyReport())
            .identifier("Bounding boxes")
            .build();

    private final InputSocket<ContoursReport> contoursSocket;
    private final OutputSocket<BoundingBoxReport<RotatedRect>> boundingBoxesSocket;

    public ContourBoundsOperation(InputSocket.Factory isf, OutputSocket.Factory osf) {
        this.contoursSocket = isf.create(contoursHint);
        this.boundingBoxesSocket = osf.create(boundingBoxesHint);
    }

    @Override
    public List<InputSocket> getInputSockets() {
        return ImmutableList.of(
            contoursSocket
        );
    }

    @Override
    public List<OutputSocket> getOutputSockets() {
        return ImmutableList.of(
            boundingBoxesSocket
        );
    }

    @Override
    public void perform() {
        ContoursReport contoursReport = contoursSocket.getValue().get();
        final int rows = contoursReport.getRows();
        final int cols = contoursReport.getCols();
        MatVector contours = contoursReport.getContours();
        List<RotatedRect> boundingBoxes = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            RotatedRect boundingBox = minAreaRect(contour);
            boundingBoxes.add(boundingBox);
        }
        boundingBoxesSocket.setValue(BoundingBoxReport.bestFitReport(rows, cols, boundingBoxes));
    }
}
