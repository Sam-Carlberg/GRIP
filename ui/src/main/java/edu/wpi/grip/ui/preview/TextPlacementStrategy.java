package edu.wpi.grip.ui.preview;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

@FunctionalInterface
public interface TextPlacementStrategy {

  /**
   * Places text above a target, and horizontally positioned as close to centered on a target
   * as possible.
   */
  TextPlacementStrategy CENTERED_ABOVE_TARGET =
      XPlacementStrategy.CLOSEST_TO_CENTER
          .withY(YPlacementStrategy.ABOVE_TARGET);

  /**
   * Places text below a target, and horizontally positioned as close to centered on a target
   * as possible.
   */
  TextPlacementStrategy CENTERED_BELOW_TARGET =
      XPlacementStrategy.CLOSEST_TO_CENTER
          .withY(YPlacementStrategy.BELOW_TARGET);

  /**
   * Places text centered directly on a target.
   */
  TextPlacementStrategy CENTERED_IN_TARGET =
      XPlacementStrategy.CLOSEST_TO_CENTER
          .withY(YPlacementStrategy.CENTERED_IN_TARGET);

  /**
   * Computes the position at which text should be placed for a target. Text is placed such that
   * the top-left corner of the text will be located at the point returned from this function.
   * All coordinates and sizes are in view-space, not image-space.
   *
   * @param viewSize     the size of the view
   * @param boundingSize the size of the bounding box
   * @param center       the center of the bounding box
   * @param textBounds   the size of the text to place
   * @param padding      the minimum padding around the text
   *
   * @return the point at which to place the text, or {@code null} if no point could be found
   */
  Point2D textPosition(Bounds viewSize,
                       Bounds boundingSize,
                       Point2D center,
                       Bounds textBounds,
                       double padding);

  /**
   * Returns a placement strategy that uses the given fallback if this strategy does not find any
   * valid text positions.
   *
   * @param fallback the fallback strategy to use
   */
  default TextPlacementStrategy orElse(TextPlacementStrategy fallback) {
    return (viewSize, boundingSize, center, textBounds, padding) -> {
      Point2D point2D = this.textPosition(viewSize, boundingSize, center, textBounds, padding);
      if (point2D == null) {
        return fallback.textPosition(viewSize, boundingSize, center, textBounds, padding);
      } else {
        return point2D;
      }
    };
  }

  /**
   * A strategy used for determining the where to place text along the X-axis of a view.
   */
  @FunctionalInterface
  interface XPlacementStrategy {

    /**
     * Places text as close to centered on a target as possible.
     */
    XPlacementStrategy CLOSEST_TO_CENTER =
        (viewSize, boundingSize, center, textBounds, padding) -> {
          boolean spaceToLeft =
              center.getX() >= textBounds.getWidth() / 2 + padding;
          boolean spaceToRight =
              center.getX() <= viewSize.getWidth() - textBounds.getWidth() / 2 - padding;

          if (spaceToLeft && spaceToRight) {
            // There's enough space to both sides for the text to be centered
            return center.getX() - textBounds.getWidth() / 2;
          } else if (spaceToRight) {
            // There's enough space to the right, but not enough to the left, so place the text as
            // far to the left as possible
            return padding;
          } else if (spaceToLeft) {
            // There's enough space to the left, but not enough to the right, so place the text as
            // far to the right as possible
            return viewSize.getWidth() - textBounds.getWidth() - padding;
          } else {
            // Not enough space anywhere!
            return -1;
          }
        };

    /**
     * Gets the X-coordinate of the text used to display the data of a target.
     *
     * @param viewSize     the size of the view
     * @param boundingSize the size of the bounding box
     * @param center       the center of the bounding box
     * @param textBounds   the size of the text to place
     * @param padding      the minimum padding around the text
     *
     * @return the X-coordinate of the text, or {@code -1} if no usable coordinate could be found
     */
    double xPosition(Bounds viewSize,
                     Bounds boundingSize,
                     Point2D center,
                     Bounds textBounds,
                     double padding);

    /**
     * Combines this X-placement strategy with a Y-placement strategy to create an overall
     * text placement strategy.
     *
     * @param yStrategy the strategy to use for determining the Y-coordinate of the text
     *
     * @return a text placement strategy
     */
    default TextPlacementStrategy withY(YPlacementStrategy yStrategy) {
      return (viewSize, boundingSize, center, textBounds, padding) -> {
        double x = this.xPosition(viewSize, boundingSize, center, textBounds, padding);
        double y = yStrategy.yPosition(viewSize, boundingSize, center, textBounds, padding);
        if (x >= 0 && y >= 0) {
          return new Point2D(x, y);
        } else {
          return null;
        }
      };
    }
  }

  /**
   * A strategy used for determining where to place text along the Y-axis of a view.
   */
  @FunctionalInterface
  interface YPlacementStrategy {

    /**
     * Places text above a target.
     */
    YPlacementStrategy ABOVE_TARGET = (viewSize, boundingSize, center, textBounds, padding) -> {
      double bbTop = center.getY() - boundingSize.getHeight() / 2;
      if (bbTop > textBounds.getHeight() + padding) {
        return bbTop - padding;
      }
      return -1;
    };

    /**
     * Places text below a target.
     */
    YPlacementStrategy BELOW_TARGET = (viewSize, boundingSize, center, textBounds, padding) -> {
      double bbBottom = center.getY() + boundingSize.getHeight() / 2;
      if (bbBottom < viewSize.getHeight() - textBounds.getHeight() - padding) {
        return bbBottom + textBounds.getHeight() + padding;
      }
      return -1;
    };

    /**
     * Places text in the center of a target.
     */
    @SuppressWarnings("ParameterName")
    YPlacementStrategy CENTERED_IN_TARGET = (_v, _b, center, textBounds, _p) -> {
      return center.getY() - textBounds.getHeight() / 2;
    };

    /**
     * Gets the Y-coordinate of the text used to display the data of a target.
     *
     * @param viewSize     the size of the view
     * @param boundingSize the size of the bounding box
     * @param center       the center of the bounding box
     * @param textBounds   the size of the text to place
     * @param padding      the minimum padding around the text
     *
     * @return the Y-coordinate of the text, or {@code -1} if no usable coordinate could be found
     */
    double yPosition(Bounds viewSize,
                     Bounds boundingSize,
                     Point2D center,
                     Bounds textBounds,
                     double padding);
  }

}
