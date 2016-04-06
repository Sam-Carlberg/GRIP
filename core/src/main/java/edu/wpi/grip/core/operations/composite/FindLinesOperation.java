package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icons;
import org.bytedeco.javacpp.indexer.FloatIndexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.LineSegmentDetector;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;

/**
 * Find line segments in a color or grayscale image
 */
public class FindLinesOperation implements Operation {

    public static final OperationDescription DESCRIPTION =
            OperationDescription.builder()
                    .constructor(FindLinesOperation::new)
                    .name("Find Lines")
                    .description("Detects line segments in an image.")
                    .category(OperationDescription.Category.FEATURE_DETECTION)
                    .icon(Icons.iconStream("find-lines"))
                    .build();

    private final SocketHint<Mat> inputHint = SocketHints.Inputs.createMatSocketHint("Input", false);
    private final SocketHint<LinesReport> linesHint = new SocketHint.Builder<>(LinesReport.class)
            .identifier("Lines").initialValueSupplier(LinesReport::new).build();


    private final InputSocket<Mat> inputSocket;

    private final OutputSocket<LinesReport> linesReportSocket;

    public FindLinesOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory) {
        this.inputSocket = inputSocketFactory.create(inputHint);
        this.linesReportSocket = outputSocketFactory.create(linesHint);
    }

    @Override
    public OperationDescription getDescription() {
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
                linesReportSocket
        };
    }

    @Override
    public Optional<Mat> createData() {
        return Optional.of(new Mat());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void perform(Optional<?> data) {
        final Mat input = inputSocket.getValue().get();
        final LineSegmentDetector lsd = linesReportSocket.getValue().get().getLineSegmentDetector();

        final Mat lines = new Mat();
        if (input.channels() == 1) {
            lsd.detect(input, lines);
        } else {
            // The line detector works on a single channel.  If the input is a color image, we can just give the line
            // detector a grayscale version of it
            final Mat tmp = (Mat) data.get();
            cvtColor(input, tmp, COLOR_BGR2GRAY);
            lsd.detect(tmp, lines);
        }

        // Store the lines in the LinesReport object
        List<LinesReport.Line> lineList = new ArrayList<>();
        if (!lines.empty()) {
            final FloatIndexer indexer = lines.<FloatIndexer>createIndexer();
            final float[] tmp = new float[4];
            for (int i = 0; i < lines.rows(); i++) {
                indexer.get(i, tmp);
                lineList.add(new LinesReport.Line(tmp[0], tmp[1], tmp[2], tmp[3]));
            }
        }

        linesReportSocket.setValue(new LinesReport(lsd, input, lineList));
    }
}
