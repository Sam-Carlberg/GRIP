package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum DistanceTransformMasksEnum {

    /** mask=3 */
    DIST_MASK_3(Imgproc.DIST_MASK_3), /** mask=5 */
    DIST_MASK_5(Imgproc.DIST_MASK_5), DIST_MASK_PRECISE(Imgproc.DIST_MASK_PRECISE);

    public final int value;

    DistanceTransformMasksEnum(int value) {
        this.value = value;
    }
}
