package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum LineSegmentDetectorModesEnum {

    /** No refinement applied */
    LSD_REFINE_NONE(Imgproc.LSD_REFINE_NONE), /** Standard refinement is applied. E.g. breaking arches into smaller straighter line approximations. */
    LSD_REFINE_STD(Imgproc.LSD_REFINE_STD), /** Advanced refinement. Number of false alarms is calculated, lines are
 *  refined through increase of precision, decrement in size, etc. */
    LSD_REFINE_ADV(Imgproc.LSD_REFINE_ADV);

    public final int value;

    LineSegmentDetectorModesEnum(int value) {
        this.value = value;
    }
}
