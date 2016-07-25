package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum BorderTypesEnum {

    /** `iiiiii|abcdefgh|iiiiiii`  with some specified `i` */
    BORDER_CONSTANT(Core.BORDER_CONSTANT), /** `aaaaaa|abcdefgh|hhhhhhh` */
    BORDER_REPLICATE(Core.BORDER_REPLICATE), /** `fedcba|abcdefgh|hgfedcb` */
    BORDER_REFLECT(Core.BORDER_REFLECT), /** `cdefgh|abcdefgh|abcdefg` */
    BORDER_WRAP(Core.BORDER_WRAP), /** `gfedcb|abcdefgh|gfedcba` */
    BORDER_REFLECT_101(Core.BORDER_REFLECT_101), /** `uvwxyz|absdefgh|ijklmno` */
    BORDER_TRANSPARENT(Core.BORDER_TRANSPARENT), /** same as BORDER_REFLECT_101 */
    BORDER_REFLECT101(Core.BORDER_REFLECT101), /** same as BORDER_REFLECT_101 */
    BORDER_DEFAULT(Core.BORDER_DEFAULT), /** do not look outside of ROI */
    BORDER_ISOLATED(Core.BORDER_ISOLATED);

    public final int value;

    BorderTypesEnum(int value) {
        this.value = value;
    }
}
