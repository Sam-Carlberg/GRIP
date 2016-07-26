package edu.wpi.grip.core.natives;

import org.opencv.core.Core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads native libraries on class init. This ensures they're only loaded once.
 */
public final class NativesLoader {

  private static final Logger log = Logger.getLogger(NativesLoader.class.getName());

  private static boolean didLoad = false;

  private NativesLoader() {}

  static {
    log.info("Loading native libraries");
    load();
  }

  /**
   * Loads native libraries. Does nothing if called more than once. This is called on class init
   * (i.e. when this class is loaded), so calling this from user code technically does nothing
   * but ensure that the class is loaded prior to OpenCV functions being used.
   */
  public static void load() {
    if (didLoad) {
      return;
    }
    log.info("java.library.path: " + System.getProperty("java.library.path"));
    try {
      // Check if the native libraries have already been loaded
      Field loaded = ClassLoader.class.getDeclaredField("loadedLibraryNames");
      loaded.setAccessible(true);
      List<String> loadedLibraries = (List) loaded.get(null); // static field
      if (!loadedLibraries.contains(Core.NATIVE_LIBRARY_NAME)) {
        copyAndLoad();
      }
      didLoad = true;
    } catch (NoSuchFieldException e) {
      // Can't know if it's already been loaded, so need to make sure
      copyAndLoad();
      didLoad = true;
    } catch (IllegalAccessException e) {
      throw new AssertionError("Couldn't check loaded libraries", e);
    }
  }

  @SuppressWarnings("all")
  private static void copyAndLoad() {
    try {
      String version =
          String.valueOf(Core.VERSION_MAJOR)
              + Core.VERSION_MINOR
              + Core.VERSION_REVISION;
      String osName = System.getProperty("os.name");
      final boolean onWindows = osName.startsWith("Windows");
      final boolean onMac = osName.startsWith("Mac");
      String resName = "/opencv-jni/";
      if (onWindows) {
        resName += "opencv_java" + version + ".dll";
      } else if (onMac) {
        resName += "libopencv_java" + version + ".dylib";
      } else {
        resName += "libopencv_java" + version + ".so";
      }
      log.info("Looking for resource: " + resName);
      File jniLib;
      InputStream is = NativesLoader.class.getResourceAsStream(resName);
      if (is != null) {
        if (onWindows) {
          jniLib = File.createTempFile("opencv_java" + version, ".dll");
        } else if (onMac) {
          jniLib = File.createTempFile("libopencv_java" + version, ".dylib");
        } else {
          jniLib = File.createTempFile("libopencv_java" + version, ".so");
        }
        jniLib.deleteOnExit();
        OutputStream os = new FileOutputStream(jniLib);
        byte[] buffer = new byte[1024];
        int readBytes;
        try {
          while ((readBytes = is.read(buffer)) != -1) {
            os.write(buffer, 0, readBytes);
          }
        } finally {
          is.close();
          os.close();
        }
        log.info("Copied native library to " + jniLib.getAbsolutePath());
        try {
          System.load(jniLib.getAbsolutePath());
        } catch (UnsatisfiedLinkError e) {
          log.log(Level.WARNING,
              String.format(
                  "Could not load native library at %s, falling back to System.loadLibrary(%s)",
                  jniLib.getAbsolutePath(),
                  Core.NATIVE_LIBRARY_NAME),
              e);
          System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
      } else {
        log.warning(String.format("No resource '%s', falling back to System.loadLibrary(\"%s\")",
            resName, Core.NATIVE_LIBRARY_NAME));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      }
    } catch (IOException e) {
      log.log(Level.WARNING, "Could not load native library", e);
    }
  }

}
