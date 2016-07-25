package edu.wpi.grip.core.operations.opencv;

import edu.wpi.grip.core.AddOperation;
import edu.wpi.grip.core.natives.NativesLoader;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;

import com.google.common.eventbus.EventBus;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class AddOperationTest {

  EventBus eventBus;
  AddOperation addition;

  @BeforeClass
  public static void loadNatives() {
    NativesLoader.load();
  }

  @Before
  public void setUp() throws Exception {
    this.eventBus = new EventBus();
    this.addition = new AddOperation(eventBus);
  }

  /**
   * This method was found here http://stackoverflow.com/a/32235744/3708426 Converted from C++ to
   * Java
   *
   * @param mat1 The first Mat
   * @param mat2 The second Mat
   * @return true if the two Matrices are the same
   */
  private boolean isMatEqual(Mat mat1, Mat mat2) {
    // treat two empty mat as identical as well
    if (mat1.empty() && mat2.empty()) {
      return true;
    }
    // if dimensionality of two mat is not identical, these two mat is not identical
    if (mat1.cols() != mat2.cols() || mat1.rows() != mat2.rows() || mat1.dims() != mat2.dims()) {
      return false;
    }
    Mat diff = new Mat();
    Core.compare(mat1, mat2, diff, Core.CMP_NE);
    int nz = Core.countNonZero(diff);
    return nz == 0;
  }

  @Test
  public void testAddMatrixOfOnesToMatrixOfTwosEqualsMatrixOfThrees() {
    // Given
    List<InputSocket> inputs = addition.getInputSockets();
    List<OutputSocket> outputs = addition.getOutputSockets();
    InputSocket a = inputs.get(0);
    InputSocket b = inputs.get(1);
    OutputSocket c = outputs.get(0);

    a.setValue(new Mat(2, 2, CvType.CV_8U, Scalar.all(1)));
    b.setValue(new Mat(2, 2, CvType.CV_8U, Scalar.all(2)));

    for (int i = 0; i < 1000; i++) {
      addition.perform();
    }

    Mat expectedResult = new Mat(2, 2, CvType.CV_8U, Scalar.all(3));
    assertTrue(isMatEqual((Mat) c.getValue().get(), expectedResult));
  }
}
