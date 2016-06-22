package edu.wpi.grip.core.operations.publishing;

import edu.wpi.grip.core.operations.composite.ContoursReport;
import edu.wpi.grip.core.operations.network.PublishAnnotatedOperation;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Point2f;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RotatedRect;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_core.Size2f;

import static org.bytedeco.javacpp.opencv_imgproc.boundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.contourArea;
import static org.bytedeco.javacpp.opencv_imgproc.convexHull;

/**
 * Manages converters for OpenCV types.
 */
public class CvConverterManager implements ConverterManager {

    /**
     * Function for converting OpenCV contours (stored in a MatVector object) to a map of str -> double[]
     */
    private static final Converter<MatVector> contourConverter = contours -> {
        Map<String, double[]> data = new LinkedHashMap<>();
        final int size = (int) contours.size();
        final double[] areas = new double[size];
        final double[] centerX = new double[size];
        final double[] centerY = new double[size];
        final double[] widths = new double[size];
        final double[] heights = new double[size];
        final double[] solidities = new double[size];
        Mat contour;
        Rect boundingRect;
        for (int i = 0; i < contours.size(); i++) {
            contour = contours.get(i);
            Mat hull = new Mat();
            convexHull(contour, hull);
            boundingRect = boundingRect(contour);
            areas[i] = contourArea(contour);
            centerX[i] = boundingRect.x() + boundingRect.width() / 2;
            centerY[i] = boundingRect.y() + boundingRect.height() / 2;
            widths[i] = boundingRect.width();
            heights[i] = boundingRect.height();
            solidities[i] = contourArea(contour) / contourArea(hull);
        }
        data.put("area", areas);
        data.put("centerX", centerX);
        data.put("centerY", centerY);
        data.put("width", widths);
        data.put("height", heights);
        data.put("solidity", solidities);
        return data;
    };

    @Override
    public void addConverters(Converters converters) {
        converters.setNamedConverter(MatVector.class, "contours", contourConverter);
        converters.setDefaultConverter(Point.class, converters.reflectiveByMethod);
        converters.setDefaultConverter(Point2f.class, converters.reflectiveByMethod);
        converters.setDefaultConverter(Size.class, converters.reflectiveByMethod);
        converters.setDefaultConverter(Size2f.class, converters.reflectiveByMethod);
        converters.setDefaultConverter(Rect.class, converters.reflectiveByMethod);
        converters.setDefaultConverter(RotatedRect.class, converters.reflectiveByMethod);
    }

}
