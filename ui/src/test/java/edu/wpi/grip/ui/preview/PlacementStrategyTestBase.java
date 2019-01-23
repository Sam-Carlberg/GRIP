package edu.wpi.grip.ui.preview;

import edu.wpi.grip.ui.preview.TextPlacementStrategy.XPlacementStrategy;
import edu.wpi.grip.ui.preview.TextPlacementStrategy.YPlacementStrategy;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

/**
 * Helper superclass for placement strategy tests.
 */
class PlacementStrategyTestBase {

  /**
   * A strategy that never finds an X-position.
   */
  static final XPlacementStrategy NEVER_X = returningX(-1);

  /**
   * A strategy that always finds an X-position.
   */
  static final XPlacementStrategy ALWAYS_X = returningX(1);

  /**
   * A strategy that never finds a Y-position.
   */
  static final YPlacementStrategy NEVER_Y = returningY(-1);

  /**
   * A strategy that always finds a Y-position.
   */
  static final YPlacementStrategy ALWAYS_Y = returningY(1);

  /**
   * A strategy that never finds a text position.
   */
  static final TextPlacementStrategy NEVER_TEXT = returningPoint(null);

  @SuppressWarnings("ParameterName")
  static XPlacementStrategy returningX(double out) {
    return (_v, _b, _c, _t, _p) -> out;
  }

  @SuppressWarnings("ParameterName")
  static YPlacementStrategy returningY(double out) {
    return (_v, _b, _c, _t, _p) -> out;
  }

  @SuppressWarnings("ParameterName")
  static TextPlacementStrategy returningPoint(Point2D point) {
    return (_v, _b, _c, _t, _p) -> point;
  }

  /**
   * Creates a new bounds object for the given width and height.
   *
   * @param width  the width of the bounds
   * @param height the height of the bounds
   *
   * @return a new bounds object
   */
  static Bounds makeBounds(double width, double height) {
    return new BoundingBox(0, 0, width, height);
  }
}
