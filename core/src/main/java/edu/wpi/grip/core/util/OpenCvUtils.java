package edu.wpi.grip.core.util;

import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to help dealing with OpenCV's sometimes questionable design choices.
 */
public final class OpenCvUtils {

  /**
   * Converts a convex hull stored in a {@code MatOfInt} to a {@code MatOfPoint} so it can be
   * used in various contour operations.
   *
   * <p>Example use:
   * <pre><code>
   *   MatOfPoint contour = ...
   *   MatOfInt hull = new MatOfInt();
   *   convexHull(contour, hull);
   *   MatOfPoint hullPoints = OpenCvUtils.hullToPoints(hull, contour);
   *   // do something with hullPoints
   * </code></pre></p>
   *
   * @param hull    the hull matrix to convert
   * @param contour the contour matrix that the hull is for
   */
  public static MatOfPoint hullToPoints(MatOfInt hull, MatOfPoint contour) {
    final List<Integer> indices = hull.toList();
    final List<Point> contourPoints = contour.toList();
    final List<Point> hullPoints = indices.stream()
        .map(contourPoints::get)
        .collect(Collectors.toList());
    final MatOfPoint out = new MatOfPoint();
    out.fromList(hullPoints);
    return out;
  }

}
