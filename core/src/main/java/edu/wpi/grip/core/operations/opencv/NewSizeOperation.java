package edu.wpi.grip.core.operations.opencv;


import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHint.View;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icon;

import org.opencv.core.Size;
import org.python.google.common.collect.ImmutableList;

import java.util.List;

public class NewSizeOperation implements CVOperation {

  public static final OperationDescription DESCRIPTION =
      CVOperation.defaultBuilder()
          .name("New Size")
          .summary("Create a size.")
          .icon(Icon.iconStream("size"))
          .build();

  private final int initWidth = -1;
  private final int initHeight = -1;
  private final SocketHint<Number> widthHint = SocketHints.Inputs
      .createNumberSpinnerSocketHint("width", initWidth, -1, Integer.MAX_VALUE);
  private final SocketHint<Number> heightHint = SocketHints.Inputs
      .createNumberSpinnerSocketHint("height", initHeight, -1, Integer.MAX_VALUE);
  private final SocketHint<Size> outputHint = new SocketHint.Builder<Size>(Size.class)
      .identifier("size")
      .initialValueSupplier(() -> new Size(initWidth, initHeight))
      .view(View.NONE).build();


  private final InputSocket<Number> widthSocket;
  private final InputSocket<Number> heightSocket;

  private final OutputSocket<Size> outputSocket;

  @SuppressWarnings("JavadocMethod")
  public NewSizeOperation(InputSocket.Factory inputSocketFactory,
                          OutputSocket.Factory outputSocketFactory) {
    this.widthSocket = inputSocketFactory.create(widthHint);
    this.heightSocket = inputSocketFactory.create(heightHint);

    this.outputSocket = outputSocketFactory.create(outputHint);
  }

  @Override
  public List<InputSocket> getInputSockets() {
    return ImmutableList.of(
        widthSocket,
        heightSocket
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
    final int widthValue = widthSocket.getValue().get().intValue();
    final int heightValue = heightSocket.getValue().get().intValue();
    outputSocket.setValue(new Size(widthValue, heightValue));
  }
}

