package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum CmpTypesEnum {

    /** src1 is equal to src2. */
    CMP_EQ(Core.CMP_EQ), /** src1 is greater than src2. */
    CMP_GT(Core.CMP_GT), /** src1 is greater than or equal to src2. */
    CMP_GE(Core.CMP_GE), /** src1 is less than src2. */
    CMP_LT(Core.CMP_LT), /** src1 is less than or equal to src2. */
    CMP_LE(Core.CMP_LE), /** src1 is unequal to src2. */
    CMP_NE(Core.CMP_NE);

    public final int value;

    CmpTypesEnum(int value) {
        this.value = value;
    }
}
