package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum RectanglesIntersectTypesEnum {

    /** No intersection */
    INTERSECT_NONE(Imgproc.INTERSECT_NONE), /** There is a partial intersection */
    INTERSECT_PARTIAL(Imgproc.INTERSECT_PARTIAL), /** One of the rectangle is fully enclosed in the other */
    INTERSECT_FULL(Imgproc.INTERSECT_FULL);

    public final int value;

    RectanglesIntersectTypesEnum(int value) {
        this.value = value;
    }
}
