package edu.wpi.grip.core.sources;

import org.opencv.core.Mat;

public interface CameraSourceUpdater {
  void setFrameRate(double value);

  void copyNewMat(Mat matToCopy);

  void updatesComplete();
}
