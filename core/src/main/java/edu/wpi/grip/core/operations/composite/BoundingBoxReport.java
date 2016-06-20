package edu.wpi.grip.core.operations.composite;

import edu.wpi.grip.core.operations.network.Publishable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RotatedRect;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A report of bounding boxes.
 *
 * @param T the type of bounding box. This is either {@link Rect} or {@link RotatedRect}.
 */
public class BoundingBoxReport<T> implements Publishable {

    private final int rows;
    private final int cols;
    private final List<T> boundingBoxes;

    private BoundingBoxReport(int rows, int cols, List<T> boundingBoxes) {
        checkNotNull(boundingBoxes, "List of bounding boxes cannot be null");
        this.rows = rows;
        this.cols = cols;
        this.boundingBoxes = boundingBoxes;
    }

    /**
     * @return the number of rows in the image that this report is for
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return the number of columns in the image that this report is for.
     */
    public int getCols() {
        return cols;
    }

    /**
     * @return the bounding boxes of this report
     */
    public List<T> getBoundingBoxes() {
        return ImmutableList.copyOf(boundingBoxes);
    }

    /**
     * Creates a report of zero bounding boxes.
     *
     * @return an empty report
     */
    public static BoundingBoxReport emptyReport() {
        return new BoundingBoxReport<>(0, 0, ImmutableList.of());
    }

    /**
     * Creates a report of best-fit bounding boxes, represented by {@link RotatedRect}.
     *
     * @param rows          the number of rows in the image
     * @param cols          the number of columns in the image
     * @param boundingBoxes the bounding boxes
     */
    public static BoundingBoxReport<RotatedRect> bestFitReport(int rows,
                                                               int cols,
                                                               List<RotatedRect> boundingBoxes) {
        return new BoundingBoxReport<>(rows, cols, ImmutableList.copyOf(boundingBoxes));
    }

    /**
     * Creates a report of simple bounding boxes (x/y aligned boxes with zero rotation).
     *
     * @param rows          the number of rows in the image
     * @param cols          the number of columns in the image
     * @param boundingBoxes the bounding boxes
     */
    public static BoundingBoxReport<Rect> simpleReport(int rows,
                                                       int cols,
                                                       List<Rect> boundingBoxes) {
        return new BoundingBoxReport<>(rows, cols, ImmutableList.copyOf(boundingBoxes));
    }


}
