package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum MorphTypesEnum {

    /** see cv::erode */
    MORPH_ERODE(Imgproc.MORPH_ERODE), /** see cv::dilate */
    MORPH_DILATE(Imgproc.MORPH_DILATE), /** an opening operation
 *  \f[\texttt{dst} = \mathrm{open} ( \texttt{src} , \texttt{element} )= \mathrm{dilate} ( \mathrm{erode} ( \texttt{src} , \texttt{element} ))\f] */
    MORPH_OPEN(Imgproc.MORPH_OPEN), /** a closing operation
 *  \f[\texttt{dst} = \mathrm{close} ( \texttt{src} , \texttt{element} )= \mathrm{erode} ( \mathrm{dilate} ( \texttt{src} , \texttt{element} ))\f] */
    MORPH_CLOSE(Imgproc.MORPH_CLOSE), /** a morphological gradient
 *  \f[\texttt{dst} = \mathrm{morph\_grad} ( \texttt{src} , \texttt{element} )= \mathrm{dilate} ( \texttt{src} , \texttt{element} )- \mathrm{erode} ( \texttt{src} , \texttt{element} )\f] */
    MORPH_GRADIENT(Imgproc.MORPH_GRADIENT), /** "top hat"
 *  \f[\texttt{dst} = \mathrm{tophat} ( \texttt{src} , \texttt{element} )= \texttt{src} - \mathrm{open} ( \texttt{src} , \texttt{element} )\f] */
    MORPH_TOPHAT(Imgproc.MORPH_TOPHAT), /** "black hat"
 *  \f[\texttt{dst} = \mathrm{blackhat} ( \texttt{src} , \texttt{element} )= \mathrm{close} ( \texttt{src} , \texttt{element} )- \texttt{src}\f] */
    MORPH_BLACKHAT(Imgproc.MORPH_BLACKHAT);

    public final int value;

    MorphTypesEnum(int value) {
        this.value = value;
    }
}
