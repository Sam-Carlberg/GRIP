package edu.wpi.grip.ui.preview;

import org.junit.Test;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import static edu.wpi.grip.ui.preview.TextPlacementStrategy.XPlacementStrategy.CLOSEST_TO_CENTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XPlacementStrategyTest extends PlacementStrategyTestBase {

  @Test
  public void testEasyCenterPlacement() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(32, 32);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 0;
    double x = CLOSEST_TO_CENTER.xPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);
    assertEquals(16, x, 0);
  }

  @Test
  public void testLeftmostPlacement() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(16, 32);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 4;
    double x = CLOSEST_TO_CENTER.xPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);
    assertEquals(padding, x, 0);
  }

  @Test
  public void testRightmostPlacement() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(48, 32);
    Bounds textBounds = makeBounds(32, 8);
    double padding = 4;
    double x = CLOSEST_TO_CENTER.xPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);
    assertEquals(28, x, 0); // 28 = 64 - 32 - 4
  }

  @Test
  public void testPlacementTextTooLong() {
    Bounds viewBounds = makeBounds(64, 64);
    Bounds boundingBoxSize = makeBounds(16, 16);
    Point2D boundingBoxCenter = new Point2D(32, 32);
    Bounds textBounds = makeBounds(56.001, 8);
    double padding = 4;
    double x = CLOSEST_TO_CENTER.xPosition(
        viewBounds, boundingBoxSize, boundingBoxCenter, textBounds, padding);
    assertEquals("The text should be to wide to place anywhere", -1, x, 0);
  }

  @Test
  @SuppressWarnings("LocalVariableName")
  public void testCombineWithY() {
    Point2D neverX_neverY = NEVER_X.withY(NEVER_Y).textPosition(null, null, null, null, 0);
    Point2D neverX_alwaysY = NEVER_X.withY(ALWAYS_Y).textPosition(null, null, null, null, 0);
    Point2D alwaysX_neverY = ALWAYS_X.withY(NEVER_Y).textPosition(null, null, null, null, 0);
    Point2D alwaysX_alwaysY = ALWAYS_X.withY(ALWAYS_Y).textPosition(null, null, null, null, 0);
    assertNull(neverX_neverY);
    assertNull(neverX_alwaysY);
    assertNull(alwaysX_neverY);
    assertEquals(new Point2D(1, 1), alwaysX_alwaysY);
  }

}
