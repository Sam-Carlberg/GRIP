package edu.wpi.grip.core.operations.opencv;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.util.Icons;

public interface CVOperation<O extends CVOperation<O>> extends Operation<O> {

    static <O extends CVOperation<O>> OperationDescription.Builder<O> defaultBuilder(Class<O> operationClass) {
        return OperationDescription.builder(operationClass)
                .category(OperationDescription.Category.OPENCV)
                .icon(Icons.iconStream("opencv"));
    }
}
