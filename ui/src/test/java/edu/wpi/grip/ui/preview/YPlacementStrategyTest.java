package edu.wpi.grip.ui.preview;

import org.junit.Test;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import static edu.wpi.grip.ui.preview.TextPlacementStrategy.YPlacementStrategy.ABOVE_TARGET;
import static edu.wpi.grip.ui.preview.TextPlacementStrategy.YPlacementStrategy.BELOW_TARGET;
import static edu.wpi.grip.ui.preview.TextPlacementStrategy.YPlacementStrategy.CENTERED_IN_TARGET;
import static org.junit.Assert.assertEquals;

public class YPlacementStrategyTest extends PlacementStrategyTestBase {

  @Test
  public void testEasyPlacementAbove() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(32, 32);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 4;
    double y = ABOVE_TARGET.yPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);

    assertEquals(20, y, 0); // 20 = 32 - (16/2) - 4
  }

  @Test
  public void testNoSpaceAbove() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(32, 16);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 4;
    double y = ABOVE_TARGET.yPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);

    assertEquals(-1, y, 0);
  }

  @Test
  public void testEasyPlacementBelow() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(32, 32);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 4;
    double y = BELOW_TARGET.yPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);

    assertEquals(52, y, 0); // 52 = 32 + 16 + 8 + 4
  }

  @Test
  public void testNoSpaceBelow() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(32, 48);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 4;
    double y = BELOW_TARGET.yPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);

    assertEquals(-1, y, 0);
  }

  @Test
  public void testCentered() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(32, 32);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 4;
    double y = CENTERED_IN_TARGET.yPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);

    assertEquals(28, y, 0); // 28 = 32 - (8/2)
  }

}
