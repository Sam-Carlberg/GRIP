package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum NormTypesEnum {

    NORM_INF(Core.NORM_INF), NORM_L1(Core.NORM_L1), NORM_L2(Core.NORM_L2), NORM_L2SQR(Core.NORM_L2SQR), NORM_HAMMING(Core.NORM_HAMMING), NORM_HAMMING2(Core.NORM_HAMMING2), NORM_TYPE_MASK(Core.NORM_TYPE_MASK), /** flag */
    NORM_RELATIVE(Core.NORM_RELATIVE), /** flag */
    NORM_MINMAX(Core.NORM_MINMAX);

    public final int value;

    NormTypesEnum(int value) {
        this.value = value;
    }
}
