package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum LineTypesEnum {

    FILLED(Core.FILLED), /** 4-connected line */
    LINE_4(Core.LINE_4), /** 8-connected line */
    LINE_8(Core.LINE_8), /** antialiased line */
    LINE_AA(Core.LINE_AA);

    public final int value;

    LineTypesEnum(int value) {
        this.value = value;
    }
}
