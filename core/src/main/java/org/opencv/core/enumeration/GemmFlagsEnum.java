package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum GemmFlagsEnum {

    /** transposes src1 */
    GEMM_1_T(Core.GEMM_1_T), /** transposes src2 */
    GEMM_2_T(Core.GEMM_2_T), /** transposes src3 */
    GEMM_3_T(Core.GEMM_3_T);

    public final int value;

    GemmFlagsEnum(int value) {
        this.value = value;
    }
}
