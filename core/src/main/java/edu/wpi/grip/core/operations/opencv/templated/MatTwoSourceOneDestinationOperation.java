package edu.wpi.grip.core.operations.opencv.templated;


import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import org.bytedeco.javacpp.opencv_core.Mat;

public class MatTwoSourceOneDestinationOperation extends TwoSourceOneDestinationOperation<Mat, Mat, Mat> {

    public MatTwoSourceOneDestinationOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory, Performer<Mat, Mat, Mat> performer, SocketHint<Mat> matSocketHint, SocketHint<Mat> matSocketHint2, SocketHint<Mat> matSocketHint3) {
        super(inputSocketFactory, outputSocketFactory, performer, matSocketHint, matSocketHint2, matSocketHint3);
    }

    public MatTwoSourceOneDestinationOperation(InputSocket.Factory inputSocketFactory, OutputSocket.Factory outputSocketFactory, Performer<Mat, Mat, Mat> performer) {
        super(inputSocketFactory, outputSocketFactory, performer, Mat.class, Mat.class, Mat.class);
    }
}
