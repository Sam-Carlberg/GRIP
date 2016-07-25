package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum DistanceTypesEnum {

    DIST_USER(Imgproc.DIST_USER), DIST_L1(Imgproc.DIST_L1), DIST_L2(Imgproc.DIST_L2), DIST_C(Imgproc.DIST_C), DIST_L12(Imgproc.DIST_L12), DIST_FAIR(Imgproc.DIST_FAIR), DIST_WELSCH(Imgproc.DIST_WELSCH), DIST_HUBER(Imgproc.DIST_HUBER);

    public final int value;

    DistanceTypesEnum(int value) {
        this.value = value;
    }
}
