package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum AdaptiveThresholdTypesEnum {

    /** the threshold value \f$T(x,y)\f$ is a mean of the \f$\texttt{blockSize} \times
    \texttt{blockSize}\f$ neighborhood of \f$(x, y)\f$ minus C */
    ADAPTIVE_THRESH_MEAN_C(Imgproc.ADAPTIVE_THRESH_MEAN_C), /** the threshold value \f$T(x, y)\f$ is a weighted sum (cross-correlation with a Gaussian
    window) of the \f$\texttt{blockSize} \times \texttt{blockSize}\f$ neighborhood of \f$(x, y)\f$
    minus C . The default sigma (standard deviation) is used for the specified blockSize . See
    cv::getGaussianKernel*/
    ADAPTIVE_THRESH_GAUSSIAN_C(Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C);

    public final int value;

    AdaptiveThresholdTypesEnum(int value) {
        this.value = value;
    }
}
