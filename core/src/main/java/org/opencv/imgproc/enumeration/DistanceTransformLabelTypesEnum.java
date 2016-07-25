package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum DistanceTransformLabelTypesEnum {

    /** each connected component of zeros in src (as well as all the non-zero pixels closest to the
    connected component) will be assigned the same label */
    DIST_LABEL_CCOMP(Imgproc.DIST_LABEL_CCOMP), /** each zero pixel (and all the non-zero pixels closest to it) gets its own label. */
    DIST_LABEL_PIXEL(Imgproc.DIST_LABEL_PIXEL);

    public final int value;

    DistanceTransformLabelTypesEnum(int value) {
        this.value = value;
    }
}
