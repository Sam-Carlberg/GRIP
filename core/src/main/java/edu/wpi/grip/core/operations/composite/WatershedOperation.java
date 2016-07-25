package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icon;

import com.google.common.collect.ImmutableList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.List;

import static org.opencv.core.Core.LINE_8;
import static org.opencv.core.Core.bitwise_not;
import static org.opencv.core.CvType.CV_32SC1;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.watershed;

/**
 * GRIP {@link Operation} for {@link org.opencv.imgproc.Imgproc#watershed}.
 */
public class WatershedOperation implements Operation {

  public static final OperationDescription DESCRIPTION =
      OperationDescription.builder()
          .name("Watershed")
          .summary("Isolates overlapping objects from the background and each other")
          .category(OperationDescription.Category.FEATURE_DETECTION)
          .icon(Icon.iconStream("opencv"))
          .build();

  private final SocketHint<Mat> srcHint = SocketHints.Inputs.createMatSocketHint("Input", false);
  private final SocketHint<ContoursReport> contoursHint = new SocketHint.Builder<>(ContoursReport
      .class)
      .identifier("Contours")
      .initialValueSupplier(ContoursReport::new)
      .build();

  private final SocketHint<Mat> outputHint = SocketHints.Inputs.createMatSocketHint("Output", true);

  private final InputSocket<Mat> srcSocket;
  private final InputSocket<ContoursReport> contoursSocket;
  private final OutputSocket<Mat> outputSocket;

  @SuppressWarnings("JavadocMethod")
  public WatershedOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory
      outputSocketFactory) {
    srcSocket = inputSocketFactory.create(srcHint);
    contoursSocket = inputSocketFactory.create(contoursHint);
    outputSocket = outputSocketFactory.create(outputHint);
  }

  @Override
  public List<InputSocket> getInputSockets() {
    return ImmutableList.of(
        srcSocket,
        contoursSocket
    );
  }

  @Override
  public List<OutputSocket> getOutputSockets() {
    return ImmutableList.of(
        outputSocket
    );
  }

  @Override
  public void perform() {
    final Mat input = srcSocket.getValue().get();
    if (input.type() != CV_8UC3) {
      throw new IllegalArgumentException("Watershed only works on 8-bit, 3-channel images");
    }

    final ContoursReport contourReport = contoursSocket.getValue().get();
    final List<MatOfPoint> contours = contourReport.getContours();

    final Mat markers = new Mat(input.size(), CV_32SC1, new Scalar(0.0));
    final Mat output = new Mat(markers.size(), CV_8UC1, new Scalar(0.0));

    try {
      // draw foreground markers (these have to be different colors)
      for (int i = 0; i < contours.size(); i++) {
        drawContours(markers, contours, i, Scalar.all((i + 1) * (255 / contours.size())),
            -1, LINE_8, null, 2, null);
      }

      // draw background marker a different color from the foreground markers
      // TODO maybe make this configurable? There may be something in the corner
      circle(markers, new Point(5, 5), 3, Scalar.all(255), -1, LINE_8, 0);

      watershed(input, markers);
      markers.convertTo(output, CV_8UC1);
      bitwise_not(output, output); // watershed inverts colors; invert them back

      outputSocket.setValue(output);
    } finally {
      // make sure that the working mat is freed to avoid a memory leak
      markers.release();
    }
  }

}
