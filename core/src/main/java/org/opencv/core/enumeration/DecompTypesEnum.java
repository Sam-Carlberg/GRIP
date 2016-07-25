package org.opencv.core.enumeration;

import org.opencv.core.Core;

public enum DecompTypesEnum {

    /** Gaussian elimination with the optimal pivot element chosen. */
    DECOMP_LU(Core.DECOMP_LU), /** singular value decomposition (SVD) method; the system can be over-defined and/or the matrix
    src1 can be singular */
    DECOMP_SVD(Core.DECOMP_SVD), /** eigenvalue decomposition; the matrix src1 must be symmetrical */
    DECOMP_EIG(Core.DECOMP_EIG), /** Cholesky \f$LL^T\f$ factorization; the matrix src1 must be symmetrical and positively
    defined */
    DECOMP_CHOLESKY(Core.DECOMP_CHOLESKY), /** QR factorization; the system can be over-defined and/or the matrix src1 can be singular */
    DECOMP_QR(Core.DECOMP_QR), /** while all the previous flags are mutually exclusive, this flag can be used together with
    any of the previous; it means that the normal equations
    \f$\texttt{src1}^T\cdot\texttt{src1}\cdot\texttt{dst}=\texttt{src1}^T\texttt{src2}\f$ are
    solved instead of the original system
    \f$\texttt{src1}\cdot\texttt{dst}=\texttt{src2}\f$ */
    DECOMP_NORMAL(Core.DECOMP_NORMAL);

    public final int value;

    DecompTypesEnum(int value) {
        this.value = value;
    }
}
