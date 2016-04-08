package edu.wpi.grip.core;

import java.util.function.Supplier;

/**
 * Holds metadata for an operation.
 */
public class OperationMetaData {

    private final OperationDescription description;
    private final Supplier<Operation> operationSupplier;

    /**
     * Creates a metadata object for an {@link Operation}.
     *
     * @param description       the description for the {@code Operation}
     * @param operationSupplier a supplier for the {@code Operation}. This should return a new instance each time it's called.
     */
    public OperationMetaData(OperationDescription description, Supplier<Operation> operationSupplier) {
        this.description = description;
        this.operationSupplier = operationSupplier;
    }

    /**
     * Gets the description of the operation.
     */
    public OperationDescription getDescription() {
        return description;
    }

    /**
     * Gets a {@code Supplier} for the operation. This should return a new instance each time it's called.
     */
    public Supplier<Operation> getOperationSupplier() {
        return operationSupplier;
    }
}
