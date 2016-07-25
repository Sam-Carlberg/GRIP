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
import org.opencv.core.Scalar;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.opencv.core.Core.inRange;

/**
 * An {@link Operation} that converts a color image into a binary image based on threshold ranges
 * for each channel.
 */
public class RGBThresholdOperation implements Operation {

  public static final OperationDescription DESCRIPTION =
      OperationDescription.builder()
          .name("RGB Threshold")
          .summary("Segment an image based on color ranges")
          .category(OperationDescription.Category.IMAGE_PROCESSING)
          .icon(Icon.iconStream("threshold"))
          .build();

  private static final Logger logger = Logger.getLogger(RGBThresholdOperation.class.getName());
  private final SocketHint<Mat> inputHint = SocketHints.Inputs.createMatSocketHint("Input", false);
  private final SocketHint<List<Number>> redHint = SocketHints.Inputs
      .createNumberListRangeSocketHint("Red", 0.0, 255.0);
  private final SocketHint<List<Number>> greenHint = SocketHints.Inputs
      .createNumberListRangeSocketHint("Green", 0.0, 255.0);
  private final SocketHint<List<Number>> blueHint = SocketHints.Inputs
      .createNumberListRangeSocketHint("Blue", 0.0, 255.0);

  private final SocketHint<Mat> outputHint = SocketHints.Outputs.createMatSocketHint("Output");


  private final InputSocket<Mat> inputSocket;
  private final InputSocket<List<Number>> redSocket;
  private final InputSocket<List<Number>> greenSocket;
  private final InputSocket<List<Number>> blueSocket;

  private final OutputSocket<Mat> outputSocket;

  @SuppressWarnings("JavadocMethod")
  public RGBThresholdOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory
      outputSocketFactory) {
    this.inputSocket = inputSocketFactory.create(inputHint);
    this.redSocket = inputSocketFactory.create(redHint);
    this.greenSocket = inputSocketFactory.create(greenHint);
    this.blueSocket = inputSocketFactory.create(blueHint);

    this.outputSocket = outputSocketFactory.create(outputHint);
  }

  @Override
  public List<InputSocket> getInputSockets() {
    return ImmutableList.of(
        inputSocket,
        redSocket,
        greenSocket,
        blueSocket
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
    final Mat input = inputSocket.getValue().get();

    if (input.channels() != 3) {
      throw new IllegalArgumentException("RGB Threshold needs a 3-channel input");
    }

    final Mat output = outputSocket.getValue().get();
    final List<Number> channel1 = redSocket.getValue().get();
    final List<Number> channel2 = greenSocket.getValue().get();
    final List<Number> channel3 = blueSocket.getValue().get();

    final Scalar lowScalar = new Scalar(
        channel3.get(0).doubleValue(),
        channel2.get(0).doubleValue(),
        channel1.get(0).doubleValue(), 0);

    final Scalar highScalar = new Scalar(
        channel3.get(1).doubleValue(),
        channel2.get(1).doubleValue(),
        channel1.get(1).doubleValue(), 0);

    try {
      inRange(input, lowScalar, highScalar, output);

      outputSocket.setValue(output);
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, e.getMessage(), e);
    }
  }
}
