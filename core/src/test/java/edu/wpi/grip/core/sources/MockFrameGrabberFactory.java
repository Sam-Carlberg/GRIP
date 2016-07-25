package edu.wpi.grip.core.sources;

import org.opencv.videoio.VideoCapture;

import java.net.MalformedURLException;

/**
 * Frame Grabber Factory that mocks out the frame grabber that it returns
 */
public class MockFrameGrabberFactory implements CameraSource.FrameGrabberFactory {

  @Override
  public VideoCapture create(int deviceNumber) {
    return new SimpleMockFrameGrabber();
  }

  @Override
  public VideoCapture create(String addressProperty) throws MalformedURLException {
    return new SimpleMockFrameGrabber();
  }
}
