package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum KmeansFlagsEnum {

    /** Select random initial centers in each attempt.*/
    KMEANS_RANDOM_CENTERS(Core.KMEANS_RANDOM_CENTERS), /** Use kmeans++ center initialization by Arthur and Vassilvitskii [Arthur2007].*/
    KMEANS_PP_CENTERS(Core.KMEANS_PP_CENTERS), /** During the first (and possibly the only) attempt, use the
        user-supplied labels instead of computing them from the initial centers. For the second and
        further attempts, use the random or semi-random centers. Use one of KMEANS_\*_CENTERS flag
        to specify the exact method.*/
    KMEANS_USE_INITIAL_LABELS(Core.KMEANS_USE_INITIAL_LABELS);

    public final int value;

    KmeansFlagsEnum(int value) {
        this.value = value;
    }
}
