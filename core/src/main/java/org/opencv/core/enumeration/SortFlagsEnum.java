package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum SortFlagsEnum {

    /** each matrix row is sorted independently */
    SORT_EVERY_ROW(Core.SORT_EVERY_ROW), /** each matrix column is sorted
 *  independently; this flag and the previous one are
 *  mutually exclusive. */
    SORT_EVERY_COLUMN(Core.SORT_EVERY_COLUMN), /** each matrix row is sorted in the ascending
 *  order. */
    SORT_ASCENDING(Core.SORT_ASCENDING), /** each matrix row is sorted in the
 *  descending order; this flag and the previous one are also
 *  mutually exclusive. */
    SORT_DESCENDING(Core.SORT_DESCENDING);

    public final int value;

    SortFlagsEnum(int value) {
        this.value = value;
    }
}
