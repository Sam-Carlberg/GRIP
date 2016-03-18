
package edu.wpi.grip.core.operations.composite;

import com.google.common.eventbus.EventBus;

import edu.wpi.grip.core.InputSocket;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OutputSocket;
import edu.wpi.grip.core.SocketHint;
import edu.wpi.grip.core.SocketHints;

import java.io.InputStream;
import java.util.Optional;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Draws contours on an image.
 */
public class DrawContoursOperation implements Operation {

    private final SocketHint<ContoursReport> contoursHint = new SocketHint.Builder<>(ContoursReport.class)
            .identifier("Contours")
            .initialValueSupplier(ContoursReport::new)
            .build();
    private final SocketHint<Boolean> colorHint = new SocketHint.Builder<>(Boolean.class)
            .identifier("Color contours")
            .initialValue(false)
            .view(SocketHint.View.CHECKBOX)
            .build();
    private final SocketHint<Number> lineThicknessHint
            = SocketHints.Inputs.createNumberSpinnerSocketHint("Line thickness", -1, -1, Integer.MAX_VALUE);

    private final SocketHint<Mat> outputHint = SocketHints.Inputs.createMatSocketHint("Mat", true);

    @Override
    public String getName() {
        return "Draw Contours";
    }

    @Override
    public String getDescription() {
        return "Draws contours on an image.\n"
                + "If 'colors' is selected, the image will be 8-bit, 3-channel; otherwise, it will be 8-bit single-channel.";
    }

    @Override
    public Category getCategory() {
        return Category.MISCELLANEOUS;
    }

    @Override
    public Optional<InputStream> getIcon() {
        return Optional.of(getClass().getResourceAsStream("/edu/wpi/grip/ui/icons/opencv.png"));
    }

    @Override
    public InputSocket<?>[] createInputSockets(EventBus eventBus) {
        return new InputSocket<?>[]{
            new InputSocket<>(eventBus, contoursHint),
            new InputSocket<>(eventBus, colorHint),
            new InputSocket<>(eventBus, lineThicknessHint)
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets(EventBus eventBus) {
        return new OutputSocket<?>[]{
            new OutputSocket<>(eventBus, outputHint)
        };
    }

    @Override
    public void perform(InputSocket<?>[] inputs, OutputSocket<?>[] outputs) {
        final ContoursReport contoursReport = (ContoursReport) inputs[0].getValue().get();
        final Boolean color = (Boolean) inputs[1].getValue().get();
        final Number lineThickness = (Number) inputs[2].getValue().get();

        final OutputSocket<Mat> outputSocket = (OutputSocket<Mat>) outputs[0];
        final Mat output = Mat.zeros(contoursReport.getRows(), contoursReport.getCols(), color ? CV_8UC3 : CV_8UC1).asMat();

        final MatVector contours = contoursReport.getContours();
        for (int i = 0; i < contours.size(); i++) {
            drawContours(output, contours, i, color ? colorForContour(i, contours.size()) : Scalar.WHITE, lineThickness.intValue(), LINE_8, null, 0, null);
        }

        outputSocket.getValue().ifPresent(Mat::release);
        outputSocket.setValue(output);
    }

    private static Scalar colorForContour(float contourNum, float numContours) {
        int rgb = java.awt.Color.HSBtoRGB((contourNum / numContours), 1, 1);
        return RGB(
                (rgb & 0xff0000) >> 16,
                (rgb & 0x00ff00) >> 8,
                (rgb & 0x0000ff));
    }

}
