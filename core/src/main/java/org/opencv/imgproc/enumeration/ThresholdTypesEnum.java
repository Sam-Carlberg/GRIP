package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum ThresholdTypesEnum {

    /** \f[\texttt{dst} (x,y) =  \fork{\texttt{maxval}}{if \(\texttt{src}(x,y) > \texttt{thresh}\)}{0}{otherwise}\f] */
    THRESH_BINARY(Imgproc.THRESH_BINARY), /** \f[\texttt{dst} (x,y) =  \fork{0}{if \(\texttt{src}(x,y) > \texttt{thresh}\)}{\texttt{maxval}}{otherwise}\f] */
    THRESH_BINARY_INV(Imgproc.THRESH_BINARY_INV), /** \f[\texttt{dst} (x,y) =  \fork{\texttt{threshold}}{if \(\texttt{src}(x,y) > \texttt{thresh}\)}{\texttt{src}(x,y)}{otherwise}\f] */
    THRESH_TRUNC(Imgproc.THRESH_TRUNC), /** \f[\texttt{dst} (x,y) =  \fork{\texttt{src}(x,y)}{if \(\texttt{src}(x,y) > \texttt{thresh}\)}{0}{otherwise}\f] */
    THRESH_TOZERO(Imgproc.THRESH_TOZERO), /** \f[\texttt{dst} (x,y) =  \fork{0}{if \(\texttt{src}(x,y) > \texttt{thresh}\)}{\texttt{src}(x,y)}{otherwise}\f] */
    THRESH_TOZERO_INV(Imgproc.THRESH_TOZERO_INV), THRESH_MASK(Imgproc.THRESH_MASK), /** flag, use Otsu algorithm to choose the optimal threshold value */
    THRESH_OTSU(Imgproc.THRESH_OTSU), /** flag, use Triangle algorithm to choose the optimal threshold value */
    THRESH_TRIANGLE(Imgproc.THRESH_TRIANGLE);

    public final int value;

    ThresholdTypesEnum(int value) {
        this.value = value;
    }
}
