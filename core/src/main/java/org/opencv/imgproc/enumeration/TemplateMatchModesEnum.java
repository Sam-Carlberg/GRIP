package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum TemplateMatchModesEnum {

    /** \f[R(x,y)= \sum _{x',y'} (T(x',y')-I(x+x',y+y'))^2\f] */
    TM_SQDIFF(Imgproc.TM_SQDIFF), /** \f[R(x,y)= \frac{\sum_{x',y'} (T(x',y')-I(x+x',y+y'))^2}{\sqrt{\sum_{x',y'}T(x',y')^2 \cdot \sum_{x',y'} I(x+x',y+y')^2}}\f] */
    TM_SQDIFF_NORMED(Imgproc.TM_SQDIFF_NORMED), /** \f[R(x,y)= \sum _{x',y'} (T(x',y')  \cdot I(x+x',y+y'))\f] */
    TM_CCORR(Imgproc.TM_CCORR), /** \f[R(x,y)= \frac{\sum_{x',y'} (T(x',y') \cdot I(x+x',y+y'))}{\sqrt{\sum_{x',y'}T(x',y')^2 \cdot \sum_{x',y'} I(x+x',y+y')^2}}\f] */
    TM_CCORR_NORMED(Imgproc.TM_CCORR_NORMED), /** \f[R(x,y)= \sum _{x',y'} (T'(x',y')  \cdot I'(x+x',y+y'))\f]
 *  where
 *  \f[\begin{array}{l} T'(x',y')=T(x',y') - 1/(w  \cdot h)  \cdot \sum _{x'',y''} T(x'',y'') \\ I'(x+x',y+y')=I(x+x',y+y') - 1/(w  \cdot h)  \cdot \sum _{x'',y''} I(x+x'',y+y'') \end{array}\f] */
    TM_CCOEFF(Imgproc.TM_CCOEFF), /** \f[R(x,y)= \frac{ \sum_{x',y'} (T'(x',y') \cdot I'(x+x',y+y')) }{ \sqrt{\sum_{x',y'}T'(x',y')^2 \cdot \sum_{x',y'} I'(x+x',y+y')^2} }\f] */
    TM_CCOEFF_NORMED(Imgproc.TM_CCOEFF_NORMED);

    public final int value;

    TemplateMatchModesEnum(int value) {
        this.value = value;
    }
}
