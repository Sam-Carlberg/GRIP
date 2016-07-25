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

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_TC89_KCOS;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static org.opencv.imgproc.Imgproc.findContours;

/**
 * An {@link Operation} that, given a binary image, produces a list of contours of all of the shapes
 * in the image.
 */
public class FindContoursOperation implements Operation {

  public static final OperationDescription DESCRIPTION =
      OperationDescription.builder()
          .name("Find Contours")
          .summary("Detects contours in a binary image.")
          .category(OperationDescription.Category.FEATURE_DETECTION)
          .icon(Icon.iconStream("find-contours"))
          .build();

  private final SocketHint<Mat> inputHint =
      new SocketHint.Builder<>(Mat.class).identifier("Input").build();

  private final SocketHint<Boolean> externalHint =
      SocketHints.createBooleanSocketHint("External Only", false);

  private final SocketHint<ContoursReport> contoursHint = new SocketHint.Builder<>(ContoursReport
      .class)
      .identifier("Contours").initialValueSupplier(ContoursReport::new).build();


  private final InputSocket<Mat> inputSocket;
  private final InputSocket<Boolean> externalSocket;

  private final OutputSocket<ContoursReport> contoursSocket;

  @SuppressWarnings("JavadocMethod")
  public FindContoursOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory
      outputSocketFactory) {
    this.inputSocket = inputSocketFactory.create(inputHint);
    this.externalSocket = inputSocketFactory.create(externalHint);

    this.contoursSocket = outputSocketFactory.create(contoursHint);
  }

  @Override
  public List<InputSocket> getInputSockets() {
    return ImmutableList.of(
        inputSocket,
        externalSocket
    );
  }

  @Override
  public List<OutputSocket> getOutputSockets() {
    return ImmutableList.of(
        contoursSocket
    );
  }

  @Override
  public void perform() {
    final Mat input = inputSocket.getValue().get();
    if (input.empty()) {
      return;
    }

    final Mat tmp = new Mat();
    final boolean externalOnly = externalSocket.getValue().get();

    // findContours modifies its input, so we pass it a temporary copy of the input image
    input.copyTo(tmp);

    // OpenCV has a few different things it can return from findContours, but for now we only use
    // EXTERNAL and LIST.
    // The other ones involve hierarchies of contours, which might be useful in some situations,
    // but probably only
    // when processing the contours manually in code (so, not in a graphical pipeline).
    List<MatOfPoint> contours = new ArrayList<>();
    findContours(tmp, contours, new Mat(), externalOnly ? RETR_EXTERNAL : RETR_LIST,
        CHAIN_APPROX_TC89_KCOS);

    contoursSocket.setValue(new ContoursReport(contours, input.rows(), input.cols()));
  }
}
