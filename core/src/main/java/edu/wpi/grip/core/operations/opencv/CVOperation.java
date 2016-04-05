package edu.wpi.grip.core.operations.opencv;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.util.Icons;

public interface CVOperation extends Operation {

    static OperationDescription.Builder defaultBuilder(Class<? extends CVOperation> operationClass) {
        return OperationDescription.builder(operationClass)
                .category(OperationDescription.Category.OPENCV)
                .icon(Icons.iconStream("opencv"));
    }
}
