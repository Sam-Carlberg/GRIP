package edu.wpi.grip.ui.preview;

import edu.wpi.grip.core.events.RenderEvent;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.ui.util.GRIPPlatform;
import edu.wpi.grip.ui.util.ImageConverter;

import com.google.common.eventbus.Subscribe;

import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Point2f;
import org.bytedeco.javacpp.opencv_core.RotatedRect;
import org.bytedeco.javacpp.opencv_core.Scalar;

import static org.bytedeco.javacpp.opencv_core.CV_8UC3;
import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_core.bitwise_xor;
import static org.bytedeco.javacpp.opencv_imgproc.line;

/**
 * Created on 6/14/16
 *
 * @author Sam Carlberg
 */
public class RotatedRectSocketPreviewView extends SocketPreviewView<List<RotatedRect>> {

    private final ImageConverter imageConverter = new ImageConverter();
    private final ImageView imageView = new ImageView();
    private final Label infoLabel = new Label();
    private final CheckBox colorRects;
    private final Mat tmp = new Mat();
    private final GRIPPlatform platform;

    private final static Scalar[] COLORS = new Scalar[] {
        Scalar.RED,
        Scalar.YELLOW,
        Scalar.GREEN,
        Scalar.CYAN,
        Scalar.BLUE,
        Scalar.MAGENTA,
    };

    /**
     * @param socket An output socket to preview
     */
    RotatedRectSocketPreviewView(GRIPPlatform platform, OutputSocket<List<RotatedRect>> socket) {
        super(socket);
        this.platform = platform;
        this.colorRects = new CheckBox("Color Rectangles");
        this.colorRects.setSelected(true);

        this.setContent(new VBox(this.imageView, this.infoLabel, this.colorRects));

        this.colorRects.selectedProperty().addListener(observable -> this.render(null));

        assert Platform.isFxApplicationThread() : "Must be in FX Thread to create this or you will be exposing constructor to another thread!";
        render(null);
    }

    @Subscribe
    private void render(RenderEvent renderEvent) {
        synchronized (this) {
            final List<RotatedRect> rects = getSocket().getValue().get();
            tmp.create(480, 640, CV_8UC3);
            bitwise_xor(tmp, tmp, tmp);
            int whichColor = 0;
            final boolean doColor = colorRects.isSelected();
            for (RotatedRect rect : rects) {
                if (rect.isNull()) {
                    // Can't draw a null rectangle, skip
                    continue;
                }
                Point2f points = new Point2f(4);
                rect.points(points);
                for (int i = 0; i < 4; i++) {
                    line(tmp, point_cast(points.position(i)), point_cast(points.position((i + 1) % 4)), doColor ? COLORS[whichColor] : Scalar.WHITE, 1, LINE_8, 0);
                }
                whichColor = (whichColor + 1) % (COLORS.length);
            }

            final Mat convertInput = tmp.clone(); // reference a copy to let the original go out of scope
            platform.runAsSoonAsPossible(() -> {
                final Image image = this.imageConverter.convert(convertInput);
                this.imageView.setImage(image);
                this.infoLabel.setText("Found " + rects.size() + " bounding boxes");
            });
        }
    }

    // The JavaCV API can be pretty stupid.
    // It doesn't include lines(Mat, Point2f, Point2f, int, int, int)
    // So we have to cast Point2f to Point in order to draw the lines
    private static Point point_cast(Point2f pt) {
        return new Point((int) pt.x(), (int) pt.y());
    }

}
