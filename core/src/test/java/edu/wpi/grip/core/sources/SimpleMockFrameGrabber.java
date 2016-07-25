package edu.wpi.grip.core.sources;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

@SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "PMD.SignatureDeclareThrowsException"})
class SimpleMockFrameGrabber extends VideoCapture {

  @Override
  public boolean grab() {
    return false;
  }

  @Override
  public boolean isOpened() {
    return false;
  }

  @Override
  public boolean open(String filename, int apiPreference) {
    return false;
  }

  @Override
  public boolean open(String filename) {
    return false;
  }

  @Override
  public boolean open(int index) {
    return false;
  }

  @Override
  public boolean read(Mat image) {
    return false;
  }

  @Override
  public boolean retrieve(Mat image, int flag) {
    return false;
  }

  @Override
  public boolean retrieve(Mat image) {
    return false;
  }

  @Override
  public boolean set(int propId, double value) {
    return false;
  }

  @Override
  public double get(int propId) {
    return 0;
  }
}
