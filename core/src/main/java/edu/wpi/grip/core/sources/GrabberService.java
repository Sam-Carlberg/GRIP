package edu.wpi.grip.core.sources;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.math.IntMath;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A service that manages the lifecycle of a {@link org.opencv.videoio.VideoCapture}.
 */
public class GrabberService extends AbstractExecutionThreadService {
  private final String name;
  private final Supplier<VideoCapture> frameGrabberSupplier;
  private final CameraSourceUpdater updater;
  private final Runnable exceptionClearedCallback;
  // Do not set this in the constructor.
  private VideoCapture frameGrabber;

  /**
   * Keep a reference to the thread around so that it can be interrupted when stop is called.
   */
  private Optional<Thread> serviceThread = Optional.empty();

  GrabberService(String name, Supplier<VideoCapture> frameGrabberSupplier, CameraSourceUpdater
      updater, Runnable exceptionClearedCallback) {
    super();
    this.name = checkNotNull(name, "Name cannot be null");
    this.frameGrabberSupplier = checkNotNull(frameGrabberSupplier, "Factory cannot be null");
    this.updater = checkNotNull(updater, "Updater cannot be null");
    this.exceptionClearedCallback = checkNotNull(exceptionClearedCallback, "Runnable cannot be "
        + "null");
  }

  @Override
  protected void startUp() throws GrabberServiceException {
    serviceThread = Optional.of(Thread.currentThread());
    try {
      frameGrabber = frameGrabberSupplier.get();
      frameGrabber.grab();
    } catch (RuntimeException ex) {
      throw new GrabberServiceException("Failed to start", ex);
    }
  }

  @Override
  protected void run() throws GrabberServiceException {
    final Stopwatch stopwatch = Stopwatch.createStarted();

    while (super.isRunning()) {
      runOneGrab(stopwatch);
    }
  }

  @VisibleForTesting
  final void runOneGrab(final Stopwatch stopwatch)
      throws GrabberServiceException {
    final Mat videoFrame = new Mat();
    if (!frameGrabber.read(videoFrame)) {
      throw new GrabberServiceException("Failed to grab image");
    }

    if (videoFrame == null) {
      throw new GrabberServiceException("Returned a null frame Mat");
    }

    if (videoFrame.empty()) {
      throw new GrabberServiceException("Returned an empty Mat");
    }

    updater.copyNewMat(videoFrame);

    stopwatch.stop();
    final long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    stopwatch.reset();
    stopwatch.start();
    if (elapsedTime != 0) {
      updater.setFrameRate(IntMath.divide(1000, Math.toIntExact(elapsedTime), RoundingMode.DOWN));
    }

    updater.updatesComplete();
    exceptionClearedCallback.run();
  }

  @Override
  protected void shutDown() throws GrabberServiceException {
    updater.setFrameRate(0);
    updater.updatesComplete();
    try {
      frameGrabber.release();
    } catch (RuntimeException ex) {
      throw new GrabberServiceException("Failed to stop", ex);
    }
  }

  @Override
  protected void triggerShutdown() {
    serviceThread.ifPresent(Thread::interrupt);
  }

  /*
   * Allows us to set our own service name
   */
  @Override
  protected String serviceName() {
    return name + " Service";
  }

  public final class GrabberServiceException extends IOException {

    GrabberServiceException(String message, Exception cause) {
      super("[" + name + "] " + message, cause);
    }

    GrabberServiceException(String message) {
      super("[" + name + "] " + message);
    }
  }
}
