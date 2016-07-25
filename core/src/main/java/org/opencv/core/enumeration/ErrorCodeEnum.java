package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum ErrorCodeEnum {

    /** everithing is ok */
    StsOk(Core.StsOk), /** pseudo error for back trace */
    StsBackTrace(Core.StsBackTrace), /** unknown /unspecified error */
    StsError(Core.StsError), /** internal error (bad state) */
    StsInternal(Core.StsInternal), /** insufficient memory */
    StsNoMem(Core.StsNoMem), /** function arg/param is bad */
    StsBadArg(Core.StsBadArg), /** unsupported function */
    StsBadFunc(Core.StsBadFunc), /** iter. didn't converge */
    StsNoConv(Core.StsNoConv), /** tracing */
    StsAutoTrace(Core.StsAutoTrace), /** image header is NULL */
    HeaderIsNull(Core.HeaderIsNull), /** image size is invalid */
    BadImageSize(Core.BadImageSize), /** offset is invalid */
    BadOffset(Core.BadOffset), BadDataPtr(Core.BadDataPtr), BadStep(Core.BadStep), BadModelOrChSeq(Core.BadModelOrChSeq), BadNumChannels(Core.BadNumChannels), BadNumChannel1U(Core.BadNumChannel1U), BadDepth(Core.BadDepth), BadAlphaChannel(Core.BadAlphaChannel), BadOrder(Core.BadOrder), BadOrigin(Core.BadOrigin), BadAlign(Core.BadAlign), BadCallBack(Core.BadCallBack), BadTileSize(Core.BadTileSize), BadCOI(Core.BadCOI), BadROISize(Core.BadROISize), MaskIsTiled(Core.MaskIsTiled), /** null pointer */
    StsNullPtr(Core.StsNullPtr), /** incorrect vector length */
    StsVecLengthErr(Core.StsVecLengthErr), /** incorr. filter structure content */
    StsFilterStructContentErr(Core.StsFilterStructContentErr), /** incorr. transform kernel content */
    StsKernelStructContentErr(Core.StsKernelStructContentErr), /** incorrect filter ofset value */
    StsFilterOffsetErr(Core.StsFilterOffsetErr), /** the input/output structure size is incorrect */
    StsBadSize(Core.StsBadSize), /** division by zero */
    StsDivByZero(Core.StsDivByZero), /** in-place operation is not supported */
    StsInplaceNotSupported(Core.StsInplaceNotSupported), /** request can't be completed */
    StsObjectNotFound(Core.StsObjectNotFound), /** formats of input/output arrays differ */
    StsUnmatchedFormats(Core.StsUnmatchedFormats), /** flag is wrong or not supported */
    StsBadFlag(Core.StsBadFlag), /** bad CvPoint */
    StsBadPoint(Core.StsBadPoint), /** bad format of mask (neither 8uC1 nor 8sC1) */
    StsBadMask(Core.StsBadMask), /** sizes of input/output structures do not match */
    StsUnmatchedSizes(Core.StsUnmatchedSizes), /** the data format/type is not supported by the function */
    StsUnsupportedFormat(Core.StsUnsupportedFormat), /** some of parameters are out of range */
    StsOutOfRange(Core.StsOutOfRange), /** invalid syntax/structure of the parsed file */
    StsParseError(Core.StsParseError), /** the requested function/feature is not implemented */
    StsNotImplemented(Core.StsNotImplemented), /** an allocated block has been corrupted */
    StsBadMemBlock(Core.StsBadMemBlock), /** assertion failed */
    StsAssert(Core.StsAssert), GpuNotSupported(Core.GpuNotSupported), GpuApiCallError(Core.GpuApiCallError), OpenGlNotSupported(Core.OpenGlNotSupported), OpenGlApiCallError(Core.OpenGlApiCallError), OpenCLApiCallError(Core.OpenCLApiCallError), OpenCLDoubleNotSupported(Core.OpenCLDoubleNotSupported), OpenCLInitError(Core.OpenCLInitError), OpenCLNoAMDBlasFft(Core.OpenCLNoAMDBlasFft);

    public final int value;

    ErrorCodeEnum(int value) {
        this.value = value;
    }
}
