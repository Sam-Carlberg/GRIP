package org.opencv.imgproc.enumeration;

import org.opencv.imgproc.Imgproc;

public enum RetrievalModesEnum {

    /** retrieves only the extreme outer contours. It sets `hierarchy[i][2]=hierarchy[i][3]=-1` for
    all the contours. */
    RETR_EXTERNAL(Imgproc.RETR_EXTERNAL), /** retrieves all of the contours without establishing any hierarchical relationships. */
    RETR_LIST(Imgproc.RETR_LIST), /** retrieves all of the contours and organizes them into a two-level hierarchy. At the top
    level, there are external boundaries of the components. At the second level, there are
    boundaries of the holes. If there is another contour inside a hole of a connected component, it
    is still put at the top level. */
    RETR_CCOMP(Imgproc.RETR_CCOMP), /** retrieves all of the contours and reconstructs a full hierarchy of nested contours.*/
    RETR_TREE(Imgproc.RETR_TREE), RETR_FLOODFILL(Imgproc.RETR_FLOODFILL);

    public final int value;

    RetrievalModesEnum(int value) {
        this.value = value;
    }
}
