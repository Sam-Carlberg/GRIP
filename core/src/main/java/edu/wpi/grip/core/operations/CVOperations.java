package edu.wpi.grip.core.operations;


import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import edu.wpi.grip.core.OperationMetaData;
import edu.wpi.grip.core.events.OperationAddedEvent;
import edu.wpi.grip.core.operations.opencv.CVOperation;
import edu.wpi.grip.core.operations.opencv.enumeration.FlipCode;
import edu.wpi.grip.core.operations.opencv.templated.*;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.generated.opencv_core.enumeration.BorderTypesEnum;
import edu.wpi.grip.generated.opencv_core.enumeration.CmpTypesEnum;
import edu.wpi.grip.generated.opencv_core.enumeration.LineTypesEnum;
import edu.wpi.grip.generated.opencv_imgproc.enumeration.AdaptiveThresholdTypesEnum;
import edu.wpi.grip.generated.opencv_imgproc.enumeration.ColorConversionCodesEnum;
import edu.wpi.grip.generated.opencv_imgproc.enumeration.ColormapTypesEnum;
import edu.wpi.grip.generated.opencv_imgproc.enumeration.ThresholdTypesEnum;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;

/**
 * A list of all of the raw opencv operations
 */
public class CVOperations {
    private final EventBus eventBus;
    private final ImmutableList<OperationMetaData> coreOperations;
    private final ImmutableList<OperationMetaData> imgprocOperation;

    @Inject
    CVOperations(EventBus eventBus, InputSocket.Factory isf, OutputSocket.Factory osf) {
        this.eventBus = eventBus;
        this.coreOperations = ImmutableList.of(
                new OperationMetaData(CVOperation.defaults("CV absdiff", "Calculate the per-element absolute difference of two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::absdiff)),

                new OperationMetaData(CVOperation.defaults("CV add", "Calculate the per-pixel sum of two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::add)),

                new OperationMetaData(CVOperation.defaults("CV addWeighted", "Calculate the weighted sum of two images."),
                        () -> new FiveSourceOneDestinationOperation<>(isf, osf,
                                (src1, alpha, src2, beta, gamma, dst) -> {
                                    opencv_core.addWeighted(src1, alpha.doubleValue(), src2, beta.doubleValue(), gamma.doubleValue(), dst);
                                },
                                SocketHints.Inputs.createMatSocketHint("src1", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("alpha", 0),
                                SocketHints.Inputs.createMatSocketHint("src2", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("beta", 0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("gamma", 0),
                                SocketHints.Outputs.createMatSocketHint("dst"))),

                new OperationMetaData(CVOperation.defaults("CV bitwise_and", "Calculate the per-element bitwise conjunction of two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::bitwise_and)),

                new OperationMetaData(CVOperation.defaults("CV bitwise_not", "Calculate per-element bit-wise inversion of an image."),
                        () -> new OneSourceOneDestinationOperation<>(isf, osf, opencv_core::bitwise_not, Mat.class, Mat.class)),

                new OperationMetaData(CVOperation.defaults("CV bitwise_or", "Calculate the per-element bit-wise disjunction of two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::bitwise_or)),

                new OperationMetaData(CVOperation.defaults("CV bitwise_xor", "Calculate the per-element bit-wise \"exclusive or\" on two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::bitwise_xor)),

                new OperationMetaData(CVOperation.defaults("CV compare", "Compare each pixel in two images using a given rule."),
                        () -> new ThreeSourceOneDestinationOperation<>(isf, osf,
                                (src1, src2, cmp, dst) -> {
                                    opencv_core.compare(src1, src2, dst, cmp.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src1", false),
                                SocketHints.Inputs.createMatSocketHint("src2", false),
                                SocketHints.createEnumSocketHint("cmpop", CmpTypesEnum.CMP_EQ),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV divide", "Perform per-pixel division of two images."),
                        () -> new ThreeSourceOneDestinationOperation<>(isf, osf,
                                (src1, src2, scale, dst) -> {
                                    opencv_core.divide(src1, src2, dst, scale.doubleValue(), -1);
                                },
                                SocketHints.Inputs.createMatSocketHint("src1", false),
                                SocketHints.Inputs.createMatSocketHint("src2", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("scale", 1.0, -Double.MAX_VALUE, Double.MAX_VALUE),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV extractChannel", "Extract a single channel from a image."),
                        () -> new TwoSourceOneDestinationOperation<>(isf, osf,
                                (src1, coi, dst) -> {
                                    opencv_core.extractChannel(src1, dst, coi.intValue());
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("channel", 0, 0, Integer.MAX_VALUE),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV flip", "Flip image around vertical, horizontal, or both axes."),
                        () -> new TwoSourceOneDestinationOperation<>(isf, osf,
                                (src, flipCode, dst) -> {
                                    opencv_core.flip(src, dst, flipCode.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.createEnumSocketHint("flipCode", FlipCode.Y_AXIS),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV max", "Calculate per-element maximum of two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::max)),
                new OperationMetaData(CVOperation.defaults("CV min", "Calculate the per-element minimum of two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::min)),
                new OperationMetaData(CVOperation.defaults("CV multiply", "Calculate the per-pixel scaled product of two images."),
                        () -> new ThreeSourceOneDestinationOperation<>(isf, osf,
                                (src1, src2, scale, dst) -> {
                                    opencv_core.multiply(src1, src2, dst, scale.doubleValue(), -1);
                                },
                                SocketHints.Inputs.createMatSocketHint("src1", false),
                                SocketHints.Inputs.createMatSocketHint("src2", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("scale", 1.0, Integer.MIN_VALUE, Integer.MAX_VALUE),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV scaleAdd", "Calculate the sum of two images where one image is multiplied by a scalar."),
                        () -> new ThreeSourceOneDestinationOperation<>(isf, osf,
                                (src1, alpha, src2, dst) -> {
                                    opencv_core.scaleAdd(src1, alpha.doubleValue(), src2, dst);
                                },
                                SocketHints.Inputs.createMatSocketHint("src1", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("scale", 1.0),
                                SocketHints.Inputs.createMatSocketHint("src2", false),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV subtract", "Calculate the per-pixel difference between two images."),
                        () -> new MatTwoSourceOneDestinationOperation(isf, osf, opencv_core::subtract)),

                new OperationMetaData(CVOperation.defaults("CV transpose", "Calculate the transpose of an image."),
                        () -> new OneSourceOneDestinationOperation<>(isf, osf, opencv_core::transpose, Mat.class, Mat.class))
        );

        // For some reason the IntelliJ compiler complains unless I do this.
        final SocketHint<AdaptiveThresholdTypesEnum> adaptiveThresholdType = SocketHints.createEnumSocketHint("adaptiveMethod", AdaptiveThresholdTypesEnum.ADAPTIVE_THRESH_MEAN_C);
        this.imgprocOperation = ImmutableList.of(
                new OperationMetaData(CVOperation.defaults("CV adaptiveThreshold", "Transforms a grayscale image to a binary image)."),
                        () -> new SixSourceOneDestinationOperation<>(isf, osf,
                                (src, maxValue, adaptiveMethod, thresholdType, blockSize, C, dst) -> {
                                    opencv_imgproc.adaptiveThreshold(src, dst, maxValue.doubleValue(), adaptiveMethod.value, thresholdType.value, blockSize.intValue(), C.doubleValue());
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("maxValue", 0.0),
                                adaptiveThresholdType,
                                SocketHints.createEnumSocketHint("thresholdType", ThresholdTypesEnum.THRESH_BINARY),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("blockSize", 0.0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("C", 0.0),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV applyColorMap", "Apply a MATLAB equivalent colormap to an image."),
                        () -> new TwoSourceOneDestinationOperation<>(isf, osf,
                                (src, colormap, dst) -> {
                                    opencv_imgproc.applyColorMap(src, dst, colormap.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.createEnumSocketHint("colormap", ColormapTypesEnum.COLORMAP_AUTUMN),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV Canny", "Apply a \"canny edge detection\" algorithm to an image."),
                        () -> new FiveSourceOneDestinationOperation<>(isf, osf,
                                (image, threshold1, threshold2, apertureSize, L2gradient, edges) -> {
                                    opencv_imgproc.Canny(image, edges, threshold1.doubleValue(), threshold2.doubleValue(), apertureSize.intValue(), L2gradient);
                                },
                                SocketHints.Inputs.createMatSocketHint("image", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("threshold1", 0.0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("threshold2", 0.0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("apertureSize", 3),
                                SocketHints.Inputs.createCheckboxSocketHint("L2gradient", false),
                                SocketHints.Outputs.createMatSocketHint("edges")
                        )),

                new OperationMetaData(CVOperation.defaults("CV cvtColor", "Convert an image from one color space to another."),
                        () -> new TwoSourceOneDestinationOperation<>(isf, osf,
                                (src, code, dst) -> {
                                    opencv_imgproc.cvtColor(src, dst, code.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.createEnumSocketHint("code", ColorConversionCodesEnum.COLOR_BGR2BGRA),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV dilate", "Expands areas of higher values in an image."),
                        () -> new SixSourceOneDestinationOperation<>(isf, osf,
                                (src, kernel, anchor, iterations, borderType, borderValue, dst) -> {
                                    opencv_imgproc.dilate(src, kernel, dst, anchor, iterations.intValue(), borderType.value, borderValue);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createMatSocketHint("kernel", true),
                                new SocketHint.Builder<>(Point.class).identifier("anchor").initialValueSupplier(Point::new).build(),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("iterations", 1),
                                SocketHints.createEnumSocketHint("borderType", BorderTypesEnum.BORDER_CONSTANT),
                                new SocketHint.Builder<>(Scalar.class).identifier("borderValue").initialValueSupplier(opencv_imgproc::morphologyDefaultBorderValue).build(),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV GaussianBlur", "Apply a Gaussian blur to an image."),
                        () -> new FiveSourceOneDestinationOperation<>(isf, osf,
                                (src, ksize, sigmaX, sigmaY, borderType, dst) -> {
                                    opencv_imgproc.GaussianBlur(src, dst, ksize, sigmaX.doubleValue(), sigmaY.doubleValue(), borderType.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", true),
                                new SocketHint.Builder<>(Size.class).identifier("ksize").initialValueSupplier(() -> new Size(1, 1)).build(),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("sigmaX", 0.0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("sigmaY", 0.0),
                                SocketHints.createEnumSocketHint("borderType", BorderTypesEnum.BORDER_DEFAULT),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV Laplacian", "Find edges by calculating the Laplacian for the given image."),
                        () -> new FiveSourceOneDestinationOperation<>(isf, osf,
                                (src, ksize, scale, delta, borderType, dst) -> {
                                    opencv_imgproc.Laplacian(src, dst, 0, ksize.intValue(), scale.doubleValue(), delta.doubleValue(), borderType.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("ksize", 1),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("scale", 1.0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("delta", 0.0),
                                SocketHints.createEnumSocketHint("borderType", BorderTypesEnum.BORDER_DEFAULT),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV medianBlur", "Apply a Median blur to an image."),
                        () -> new TwoSourceOneDestinationOperation<>(isf, osf,
                                (src, ksize, dst) -> {
                                    opencv_imgproc.medianBlur(src, dst, ksize.intValue());
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("ksize", 1, 1, Integer.MAX_VALUE),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV rectangle", "Draw a rectangle (outline or filled) on an image."),
                        () -> new SevenSourceOneDestinationOperation<>(isf, osf,
                                (src, pt1, pt2, color, thickness, lineType, shift, dst) -> {
                                    src.copyTo(dst); // Rectangle only has one input and it modifies it so we have to copy the input image to the dst
                                    opencv_imgproc.rectangle(dst, pt1, pt2, color, thickness.intValue(), lineType.value, shift.intValue());
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createPointSocketHint("pt1", 0, 0),
                                SocketHints.Inputs.createPointSocketHint("pt2", 0, 0),
                                new SocketHint.Builder<>(Scalar.class).identifier("color").initialValueSupplier(() -> Scalar.BLACK).build(),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("thickness", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
                                SocketHints.createEnumSocketHint("lineType", LineTypesEnum.LINE_8),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("shift", 0),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV Sobel", "Find edges by calculating the requested derivative order for the given image."),
                        () -> new SevenSourceOneDestinationOperation<>(isf, osf,
                                (src, dx, dy, ksize, scale, delta, borderType, dst) -> {
                                    opencv_imgproc.Sobel(src, dst, 0, dx.intValue(), dy.intValue(), ksize.intValue(), scale.doubleValue(), delta.doubleValue(), borderType.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("dx", 0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("dy", 0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("ksize", 3),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("scale", 1),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("delta", 0),
                                SocketHints.createEnumSocketHint("borderType", BorderTypesEnum.BORDER_DEFAULT),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        )),

                new OperationMetaData(CVOperation.defaults("CV Threshold", "Apply a fixed-level threshold to each array element in an image."),
                        () -> new FourSourceOneDestinationOperation<>(isf, osf,
                                (src, thresh, maxval, type, dst) -> {
                                    opencv_imgproc.threshold(src, dst, thresh.doubleValue(), maxval.doubleValue(), type.value);
                                },
                                SocketHints.Inputs.createMatSocketHint("src", false),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("thresh", 0),
                                SocketHints.Inputs.createNumberSpinnerSocketHint("maxval", 0),
                                SocketHints.createEnumSocketHint("type", ThresholdTypesEnum.THRESH_BINARY),
                                SocketHints.Outputs.createMatSocketHint("dst")
                        ))
        );
    }

    public void addOperations() {
        coreOperations.stream()
                .map(OperationAddedEvent::new)
                .forEach(eventBus::post);
        imgprocOperation.stream()
                .map(OperationAddedEvent::new)
                .forEach(eventBus::post);
    }
}
