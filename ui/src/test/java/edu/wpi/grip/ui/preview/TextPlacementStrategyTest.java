package edu.wpi.grip.ui.preview;

import org.junit.Test;

import javafx.geometry.Point2D;

import static org.junit.Assert.assertSame;

public class TextPlacementStrategyTest extends PlacementStrategyTestBase {

  @Test
  public void testFallbackFromNull() {
    Point2D fallbackPoint = new Point2D(0, 0);
    TextPlacementStrategy fallback = returningPoint(fallbackPoint);
    Point2D point2D = NEVER_TEXT.orElse(fallback)
        .textPosition(null, null, null, null, 0);
    assertSame(fallbackPoint, point2D);
  }

  @Test
  public void testFallbackFromNonNull() {
    Point2D pt = new Point2D(0, 0);
    TextPlacementStrategy strat = returningPoint(pt);
    Point2D point2D = strat.orElse(NEVER_TEXT)
        .textPosition(null, null, null, null, 0);
    assertSame(pt, point2D);
  }

}
