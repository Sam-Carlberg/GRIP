package edu.wpi.grip.ui.preview;

import edu.wpi.grip.core.operations.composite.ContoursReport;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.ui.util.GripPlatform;

import java.util.Optional;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static org.bytedeco.javacpp.opencv_core.CV_8UC3;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.Scalar;
import static org.bytedeco.javacpp.opencv_core.bitwise_xor;
import static org.bytedeco.javacpp.opencv_imgproc.drawContours;

/**
 * A preview view for displaying contours.  This view shows each contour as a different-colored
 * outline (so they can be individually distinguished), as well as a count of the total number of
 * contours found.
 */
public final class ContoursSocketPreviewView extends ImageBasedPreviewView<ContoursReport> {

  private static final Scalar[] CONTOUR_COLORS = new Scalar[]{
      Scalar.RED,
      Scalar.YELLOW,
      Scalar.GREEN,
      Scalar.CYAN,
      Scalar.BLUE,
      Scalar.MAGENTA,
  };
  private final Label infoLabel = new Label();
  private final CheckBox colorContours;
  private final Mat tmp = new Mat();
  private final GripPlatform platform;
  private final StackPane stackPane;
  private final Rectangle boundingBox;
  private final Line angleLine;

  @SuppressWarnings("PMD.ImmutableField")
  private double lastX = -1;
  @SuppressWarnings("PMD.ImmutableField")
  private double lastY = -1;

  private final Text text;

  private final TextPlacementStrategy textPlacementStrategy =
      TextPlacementStrategy.CENTERED_ABOVE_TARGET              // Prefer text to be above contours
          .orElse(TextPlacementStrategy.CENTERED_BELOW_TARGET) // Fall back to below contours
          .orElse(TextPlacementStrategy.CENTERED_IN_TARGET);   // Bail and place in the center

  /**
   * @param socket An output socket to preview.
   */
  public ContoursSocketPreviewView(GripPlatform platform, OutputSocket<ContoursReport> socket) {
    super(socket);
    this.platform = platform;
    this.colorContours = new CheckBox("Color Contours");
    this.colorContours.setSelected(false);

    boundingBox = new Rectangle();
    boundingBox.setFill(Color.color(1, 1, 0, 0.1));
    boundingBox.setStroke(Color.YELLOW);
    boundingBox.setManaged(false);
    boundingBox.setMouseTransparent(true);
    angleLine = new Line();
    angleLine.setFill(null);
    angleLine.setStroke(Color.CYAN);
    angleLine.setManaged(false);
    angleLine.setMouseTransparent(true);
    stackPane = new StackPane(this.imageView);
    text = new Text();
    text.setManaged(false);
    text.setMouseTransparent(true);
    text.setStroke(Color.gray(0.9));
    this.setContent(new VBox(stackPane, this.infoLabel, this.colorContours));

    this.imageView.setOnMouseMoved(event -> {
      lastX = event.getX();
      lastY = event.getY();
      highlightContour(lastX, lastY);
    });

    this.imageView.setOnMouseExited(event -> {
      lastX = -1;
      lastY = -1;
      highlightContour(lastX, lastY);
    });

    this.imageView.setOnMouseEntered(event -> {
      lastX = event.getX();
      lastY = event.getY();
      highlightContour(lastX, lastY);
    });

    this.colorContours.selectedProperty().addListener(observable -> this.convertImage());
  }

  /**
   * Draws a bounding box around a contour and displays information about the contour.
   *
   * @param mouseX the X-coordinate of the mouse, local to the image view
   * @param mouseY the Y-coordinate of the mouse, local to the image view
   */
  private void highlightContour(double mouseX, double mouseY) {
    if (getScene() == null || getScene().getWindow() == null) {
      return;
    }
    if (mouseX < 0 || mouseY < 0) {
      stackPane.getChildren().removeAll(boundingBox, angleLine, text);
      return;
    }
    Bounds imageBounds = imageView.getBoundsInLocal();
    ContoursReport report = getSocket().getValue().get();
    double viewWidth = imageBounds.getWidth();
    int imageX = (int) map(0, viewWidth, 0, report.getCols(), mouseX);
    double viewHeight = imageBounds.getHeight();
    int imageY = (int) map(0, viewHeight, 0, report.getRows(), mouseY);

    Optional<ContoursReport.Contour> contour = report.getProcessedContours()
        .stream()
        .filter(c -> contains(c, imageX, imageY))
        .findFirst();
    if (contour.isPresent()) {
      // A contour was hovered over, highlight it
      ContoursReport.Contour c = contour.get();
      boundingBox.setX(map(0, report.getCols(), 0, viewWidth, c.centerX() - c.width() / 2));
      boundingBox.setY(map(0, report.getRows(), 0, viewHeight, c.centerY() - c.height() / 2));
      boundingBox.setWidth(map(0, report.getCols(), 0, viewWidth, c.width()));
      boundingBox.setHeight(map(0, report.getRows(), 0, viewHeight, c.height()));

      angleLine.setStartX(map(0, report.getCols(), 0, viewWidth, c.centerX()));
      angleLine.setStartY(map(0, report.getRows(), 0, viewHeight, c.centerY()));
      angleLine.setEndX(
          Math.cos(Math.toRadians(c.angle())) * (c.width() / 2) + angleLine.getStartX());
      angleLine.setEndY(
          Math.sin(Math.toRadians(c.angle())) * (c.height() / 2) + angleLine.getStartY());

      if (!stackPane.getChildren().contains(boundingBox)) {
        stackPane.getChildren().addAll(boundingBox, angleLine, text);
      }
      text.setText(makeInfoText(c));

      Point2D position = textPlacementStrategy.textPosition(
          imageBounds,
          boundingBox.getBoundsInLocal(),
          new Point2D(
              boundingBox.getX() + boundingBox.getWidth() / 2,
              boundingBox.getY() + boundingBox.getHeight() / 2
          ),
          text.getBoundsInLocal(),
          6
      );

      if (position != null) {
        text.setX(position.getX());
        text.setY(position.getY());
      } else {
        // TODO better corner case handling
        text.setX(mouseX);
        text.setY(mouseY);
      }
    } else {
      // No contour, remove the highlight and info
      stackPane.getChildren().removeAll(boundingBox, angleLine, text);
    }
  }

  @Override
  protected void convertImage() {
    synchronized (this) {
      final ContoursReport contours = this.getSocket().getValue().get();
      long numContours = 0;

      if (!contours.getContours().isNull() && contours.getRows() > 0 && contours.getCols() > 0) {
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
          drawContours(this.tmp, contours.getContours(), -1, Scalar.WHITE);
        }
      }

      final long finalNumContours = numContours;
      final Mat convertInput = tmp;
      platform.runAsSoonAsPossible(() -> {
        final Image image = this.imageConverter.convert(convertInput, getImageHeight());
        this.imageView.setImage(image);
        highlightContour(lastX, lastY);
        this.infoLabel.setText("Found " + finalNumContours + " contours");
      });
    }
  }

  // Helper methods

  /**
   * Maps an input {@code v} from the range {@code [minInput, maxInput]} to the range {@code
   * [minOutput, maxOutput]}.
   *
   * @param minInput  the lower bound of the input range
   * @param maxInput  the upper bound of the input range
   * @param minOutput the lower bound of the output range
   * @param maxOutput the upper bound of the output range
   * @param v         the value to map
   *
   * @return the mapped output
   */
  private static double map(double minInput,
                            double maxInput,
                            double minOutput,
                            double maxOutput,
                            double v) {
    return ((v - minInput) / (maxInput - minInput)) * (maxOutput - minOutput) + minOutput;
  }

  /**
   * Checks if the bounding box of a contour contains a given point.
   *
   * @param contour the contour to check
   * @param x       the X-coordinate of the point in image space
   * @param y       the Y-coordinate of the point in image space
   *
   * @return true if the point {@code (x, y)} is within the bounding box of the contour
   */
  private static boolean contains(ContoursReport.Contour contour, int x, int y) {
    return x >= contour.centerX() - contour.width() / 2
        && x <= contour.centerX() + contour.width() / 2
        && y >= contour.centerY() - contour.height() / 2
        && y <= contour.centerY() + contour.height() / 2;
  }

  /**
   * Generates info text for a contour.
   */
  private static String makeInfoText(ContoursReport.Contour contour) {
    return String.format(
        "(%.0f, %.0f), size [%.0fx%.0f], solidity %.2f%%, angle %.2f",
        contour.centerX(),
        contour.centerY(),
        contour.width(),
        contour.height(),
        contour.solidity() * 100,
        contour.angle()
    );
  }

}
