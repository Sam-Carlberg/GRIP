package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum InterpolationFlagsEnum {

    /** nearest neighbor interpolation */
    INTER_NEAREST(Imgproc.INTER_NEAREST), /** bilinear interpolation */
    INTER_LINEAR(Imgproc.INTER_LINEAR), /** bicubic interpolation */
    INTER_CUBIC(Imgproc.INTER_CUBIC), /** resampling using pixel area relation. It may be a preferred method for image decimation, as
    it gives moire'-free results. But when the image is zoomed, it is similar to the INTER_NEAREST
    method. */
    INTER_AREA(Imgproc.INTER_AREA), /** Lanczos interpolation over 8x8 neighborhood */
    INTER_LANCZOS4(Imgproc.INTER_LANCZOS4), /** mask for interpolation codes */
    INTER_MAX(Imgproc.INTER_MAX), /** flag, fills all of the destination image pixels. If some of them correspond to outliers in the
    source image, they are set to zero */
    WARP_FILL_OUTLIERS(Imgproc.WARP_FILL_OUTLIERS), /** flag, inverse transformation

    For example, polar transforms:
    - flag is __not__ set: \f$dst( \phi , \rho ) = src(x,y)\f$
    - flag is set: \f$dst(x,y) = src( \phi , \rho )\f$
    */
    WARP_INVERSE_MAP(Imgproc.WARP_INVERSE_MAP);

    public final int value;

    InterpolationFlagsEnum(int value) {
        this.value = value;
    }
}
