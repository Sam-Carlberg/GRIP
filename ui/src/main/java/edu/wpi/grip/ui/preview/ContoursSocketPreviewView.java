package edu.wpi.grip.ui.preview;

import edu.wpi.grip.core.events.RenderEvent;
import edu.wpi.grip.core.operations.composite.ContoursReport;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.ui.util.GripPlatform;
import edu.wpi.grip.ui.util.ImageConverter;

import com.google.common.eventbus.Subscribe;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import static org.opencv.core.Core.bitwise_xor;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.drawContours;

/**
 * A preview view for displaying contours.  This view shows each contour as a different-colored
 * outline (so they can be individually distinguished), as well as a count of the total number of
 * contours found.
 */
public final class ContoursSocketPreviewView extends SocketPreviewView<ContoursReport> {

  private static final Scalar[] CONTOUR_COLORS = new Scalar[] {
      new Scalar(0, 0, 255, 255),   // RED
      new Scalar(0, 255, 255, 255), // YELLOW
      new Scalar(0, 255, 0, 255),   // GREEN
      new Scalar(255, 150, 0, 255), // CYAN
      new Scalar(255, 0, 0, 255),   // BLUE
      new Scalar(255, 0, 255, 255), // MAGENTA
  };
  private final ImageConverter imageConverter = new ImageConverter();
  private final ImageView imageView = new ImageView();
  private final Label infoLabel = new Label();
  private final CheckBox colorContours;
  private final Mat tmp = new Mat();
  private final GripPlatform platform;

  /**
   * @param socket An output socket to preview.
   */
  public ContoursSocketPreviewView(GripPlatform platform, OutputSocket<ContoursReport> socket) {
    super(socket);
    this.platform = platform;
    this.colorContours = new CheckBox("Color Contours");
    this.colorContours.setSelected(false);

    this.setContent(new VBox(this.imageView, this.infoLabel, this.colorContours));

    this.colorContours.selectedProperty().addListener(observable -> this.render());

    assert Platform.isFxApplicationThread() : "Must be in FX Thread to create this or you will be"
        + " exposing constructor to another thread!";
    render();
  }

  @Subscribe
  public void onRender(RenderEvent event) {
    this.render();
  }

  private void render() {
    synchronized (this) {
      final ContoursReport contours = this.getSocket().getValue().get();
      long numContours = 0;

      if (contours.getRows() > 0 && contours.getCols() > 0) {
        // Allocate a completely black OpenCV Mat to draw the contours onto.  We can easily
        // render contours
        // by using OpenCV's drawContours function and converting the Mat into a JavaFX Image.
        this.tmp.create(contours.getRows(), contours.getCols(), CV_8UC3);
        bitwise_xor(tmp, tmp, tmp);

        numContours = contours.getContours().size();

        if (this.colorContours.isSelected()) {
          for (int i = 0; i < numContours; i++) {
            drawContours(this.tmp, contours.getContours(), i, CONTOUR_COLORS[i % CONTOUR_COLORS
                .length]);
          }
        } else {
          drawContours(this.tmp, contours.getContours(), -1, Scalar.all(255));
        }
      }

      final long finalNumContours = numContours;
      final Mat convertInput = tmp;
      platform.runAsSoonAsPossible(() -> {
        final Image image = this.imageConverter.convert(convertInput);
        this.imageView.setImage(image);
        this.infoLabel.setText("Found " + finalNumContours + " contours");
      });
    }
  }
}
