package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum InterpolationMasksEnum {

    INTER_BITS(Imgproc.INTER_BITS), INTER_BITS2(Imgproc.INTER_BITS2), INTER_TAB_SIZE(Imgproc.INTER_TAB_SIZE), INTER_TAB_SIZE2(Imgproc.INTER_TAB_SIZE2);

    public final int value;

    InterpolationMasksEnum(int value) {
        this.value = value;
    }
}
