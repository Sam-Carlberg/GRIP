package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum UndistortTypesEnum {

    PROJ_SPHERICAL_ORTHO(Imgproc.PROJ_SPHERICAL_ORTHO), PROJ_SPHERICAL_EQRECT(Imgproc.PROJ_SPHERICAL_EQRECT);

    public final int value;

    UndistortTypesEnum(int value) {
        this.value = value;
    }
}
