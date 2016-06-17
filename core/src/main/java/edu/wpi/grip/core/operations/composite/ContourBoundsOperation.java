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
    private final SocketHint<List<RotatedRect>> boundingBoxesHint =
        new SocketHint.Builder<>((Class<List<RotatedRect>>) new TypeToken<List<RotatedRect>>() {}.getRawType())
            .initialValue(Collections.emptyList())
            .identifier("Bounding boxes")
            .build();

    private final InputSocket<ContoursReport> contoursSocket;
    private final OutputSocket<List<RotatedRect>> boundingBoxesSocket;

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
        // Don't actually need the image; it's just for displaying the bounding boxes in the UI
        MatVector contours = contoursSocket.getValue().get().getContours();
        List<RotatedRect> boundingBoxes = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            RotatedRect boundingBox = minAreaRect(contour);
            boundingBoxes.add(boundingBox);
        }
        boundingBoxesSocket.setValue(boundingBoxes);
    }
}
