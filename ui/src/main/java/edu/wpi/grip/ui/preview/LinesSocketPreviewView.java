package edu.wpi.grip.ui.preview;

import edu.wpi.grip.core.events.RenderEvent;
import edu.wpi.grip.core.operations.composite.LinesReport;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.ui.util.GripPlatform;
import edu.wpi.grip.ui.util.ImageConverter;

import com.google.common.eventbus.Subscribe;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import static org.opencv.core.Core.bitwise_xor;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.LINE_8;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.line;

/**
 * A <code>SocketPreviewView</code> that previews sockets containing containing the result of a line
 * detection algorithm.
 */
public class LinesSocketPreviewView extends SocketPreviewView<LinesReport> {

  private final ImageConverter imageConverter = new ImageConverter();
  private final ImageView imageView = new ImageView();
  private final Label infoLabel = new Label();
  private final Mat tmp = new Mat();
  private final GripPlatform platform;
  @SuppressWarnings("PMD.ImmutableField")
  private boolean showInputImage;

  /**
   * @param socket An output socket to preview.
   */
  public LinesSocketPreviewView(GripPlatform platform, OutputSocket<LinesReport> socket) {
    super(socket);
    this.platform = platform;

    // Add a checkbox to set if the preview should just show the lines, or also the input image
    final CheckBox show = new CheckBox("Show Input Image");
    show.setSelected(this.showInputImage);
    show.selectedProperty().addListener(observable -> {
      this.showInputImage = show.isSelected();
      this.convertImage();
    });

    final VBox content = new VBox(this.imageView, new Separator(Orientation.HORIZONTAL), this
        .infoLabel, show);
    content.getStyleClass().add("preview-box");
    this.setContent(content);

    assert Platform.isFxApplicationThread() : "Must be in FX Thread to create this or you will be"
        + " exposing constructor to another thread!";
    convertImage();
  }

  @Subscribe
  public void onRender(RenderEvent event) {
    this.convertImage();
  }

  private void convertImage() {
    synchronized (this) {
      final LinesReport linesReport = this.getSocket().getValue().get();
      final List<LinesReport.Line> lines = linesReport.getLines();
      Mat input = linesReport.getInput();

      // If there were lines found, draw them on the image before displaying it
      if (!linesReport.getLines().isEmpty()) {
        if (input.channels() == 3) {
          input.copyTo(tmp);
        } else {
          cvtColor(input, tmp, COLOR_GRAY2BGR);
        }

        input = tmp;

        // If we don't want to see the background image, set it to black
        if (!this.showInputImage) {
          bitwise_xor(tmp, tmp, tmp);
        }

        // For each line in the report, draw a line along with the starting and ending points
        for (LinesReport.Line line : lines) {
          final Point startPoint = new Point((int) line.x1, (int) line.y1);
          final Point endPoint = new Point((int) line.x2, (int) line.y2);
          line(input, startPoint, endPoint, Scalar.all(255), 2, LINE_8, 0);
          circle(input, startPoint, 2, Scalar.all(255), 2, LINE_8, 0);
          circle(input, endPoint, 2, Scalar.all(255), 2, LINE_8, 0);
        }
      }
      final Mat convertInput = input;
      final int numLines = lines.size();
      platform.runAsSoonAsPossible(() -> {
        final Image image = this.imageConverter.convert(convertInput);
        this.imageView.setImage(image);
        this.infoLabel.setText("Found " + numLines + " lines");
      });
    }
  }
}
