package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum ColormapTypesEnum {

    /** ![autumn](pics/colormaps/colorscale_autumn.jpg) */
    COLORMAP_AUTUMN(Imgproc.COLORMAP_AUTUMN), /** ![bone](pics/colormaps/colorscale_bone.jpg) */
    COLORMAP_BONE(Imgproc.COLORMAP_BONE), /** ![jet](pics/colormaps/colorscale_jet.jpg) */
    COLORMAP_JET(Imgproc.COLORMAP_JET), /** ![winter](pics/colormaps/colorscale_winter.jpg) */
    COLORMAP_WINTER(Imgproc.COLORMAP_WINTER), /** ![rainbow](pics/colormaps/colorscale_rainbow.jpg) */
    COLORMAP_RAINBOW(Imgproc.COLORMAP_RAINBOW), /** ![ocean](pics/colormaps/colorscale_ocean.jpg) */
    COLORMAP_OCEAN(Imgproc.COLORMAP_OCEAN), /** ![summer](pics/colormaps/colorscale_summer.jpg) */
    COLORMAP_SUMMER(Imgproc.COLORMAP_SUMMER), /** ![spring](pics/colormaps/colorscale_spring.jpg) */
    COLORMAP_SPRING(Imgproc.COLORMAP_SPRING), /** ![cool](pics/colormaps/colorscale_cool.jpg) */
    COLORMAP_COOL(Imgproc.COLORMAP_COOL), /** ![HSV](pics/colormaps/colorscale_hsv.jpg) */
    COLORMAP_HSV(Imgproc.COLORMAP_HSV), /** ![pink](pics/colormaps/colorscale_pink.jpg) */
    COLORMAP_PINK(Imgproc.COLORMAP_PINK), /** ![hot](pics/colormaps/colorscale_hot.jpg) */
    COLORMAP_HOT(Imgproc.COLORMAP_HOT), /** ![hot](pics/colormaps/colorscale_parula.jpg) */
    COLORMAP_PARULA(Imgproc.COLORMAP_PARULA);

    public final int value;

    ColormapTypesEnum(int value) {
        this.value = value;
    }
}
