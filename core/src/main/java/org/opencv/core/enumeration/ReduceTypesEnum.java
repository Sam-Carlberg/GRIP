package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum ReduceTypesEnum {

    /** the output is the sum of all rows/columns of the matrix. */
    REDUCE_SUM(Core.REDUCE_SUM), /** the output is the mean vector of all rows/columns of the matrix. */
    REDUCE_AVG(Core.REDUCE_AVG), /** the output is the maximum (column/row-wise) of all rows/columns of the matrix. */
    REDUCE_MAX(Core.REDUCE_MAX), /** the output is the minimum (column/row-wise) of all rows/columns of the matrix. */
    REDUCE_MIN(Core.REDUCE_MIN);

    public final int value;

    ReduceTypesEnum(int value) {
        this.value = value;
    }
}
