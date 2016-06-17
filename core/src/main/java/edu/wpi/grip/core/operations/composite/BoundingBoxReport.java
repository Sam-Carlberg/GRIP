package edu.wpi.grip.core.operations.composite;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RotatedRect;

/**
 *
 */
public class BoundingBoxReport<T> {

    private final int rows;
    private final int cols;
    private List<T> boundingBoxes;

    private BoundingBoxReport(int rows, int cols, List<T> boundingBoxes) {
        this.rows = rows;
        this.cols = cols;
        this.boundingBoxes = boundingBoxes;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<T> getBoundingBoxes() {
        return boundingBoxes;
    }

    static BoundingBoxReport emptyReport() {
        return new BoundingBoxReport<>(0, 0, ImmutableList.of());
    }

    public static BoundingBoxReport<RotatedRect> bestFitReport(int rows,
                                                               int cols,
                                                               List<RotatedRect> rectangles) {
        return new BoundingBoxReport<>(rows, cols, ImmutableList.copyOf(rectangles));
    }

    public static BoundingBoxReport<Rect> simpleReport(int rows,
                                                       int cols,
                                                       List<Rect> rectangles) {
        return new BoundingBoxReport<>(rows, cols, ImmutableList.copyOf(rectangles));
    }


}
