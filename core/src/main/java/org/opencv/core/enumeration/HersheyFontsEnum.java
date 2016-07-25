package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum HersheyFontsEnum {

    /** normal size sans-serif font */
    FONT_HERSHEY_SIMPLEX(Core.FONT_HERSHEY_SIMPLEX), /** small size sans-serif font */
    FONT_HERSHEY_PLAIN(Core.FONT_HERSHEY_PLAIN), /** normal size sans-serif font (more complex than FONT_HERSHEY_SIMPLEX) */
    FONT_HERSHEY_DUPLEX(Core.FONT_HERSHEY_DUPLEX), /** normal size serif font */
    FONT_HERSHEY_COMPLEX(Core.FONT_HERSHEY_COMPLEX), /** normal size serif font (more complex than FONT_HERSHEY_COMPLEX) */
    FONT_HERSHEY_TRIPLEX(Core.FONT_HERSHEY_TRIPLEX), /** smaller version of FONT_HERSHEY_COMPLEX */
    FONT_HERSHEY_COMPLEX_SMALL(Core.FONT_HERSHEY_COMPLEX_SMALL), /** hand-writing style font */
    FONT_HERSHEY_SCRIPT_SIMPLEX(Core.FONT_HERSHEY_SCRIPT_SIMPLEX), /** more complex variant of FONT_HERSHEY_SCRIPT_SIMPLEX */
    FONT_HERSHEY_SCRIPT_COMPLEX(Core.FONT_HERSHEY_SCRIPT_COMPLEX), /** flag for italic font */
    FONT_ITALIC(Core.FONT_ITALIC);

    public final int value;

    HersheyFontsEnum(int value) {
        this.value = value;
    }
}
