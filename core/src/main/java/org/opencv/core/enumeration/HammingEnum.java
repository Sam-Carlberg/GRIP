package org.opencv.core.enumeration;

import org.opencv.features2d.DescriptorMatcher;

public enum HammingEnum {

  normType(DescriptorMatcher.BRUTEFORCE_HAMMING);

  public final int value;

  HammingEnum(int value) {
    this.value = value;
  }
}
