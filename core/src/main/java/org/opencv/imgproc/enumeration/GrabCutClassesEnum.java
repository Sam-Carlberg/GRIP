package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum GrabCutClassesEnum {

    /** an obvious background pixels */
    GC_BGD(Imgproc.GC_BGD), /** an obvious foreground (object) pixel */
    GC_FGD(Imgproc.GC_FGD), /** a possible background pixel */
    GC_PR_BGD(Imgproc.GC_PR_BGD), /** a possible foreground pixel */
    GC_PR_FGD(Imgproc.GC_PR_FGD);

    public final int value;

    GrabCutClassesEnum(int value) {
        this.value = value;
    }
}
