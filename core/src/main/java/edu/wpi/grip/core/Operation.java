package edu.wpi.grip.core;

import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketsProvider;

import java.util.Optional;

/**
 * The common interface used by <code>Step</code>s in a pipeline to call various operations.  There is usually only one
 * instance of any class that implements <code>Operation</code>, which is called whenever that operation is used.
 */
public interface Operation {

    /**
     * Factory that creates an {@code Operation} instance from an input socket factory and an output socket factory.
     *
     * @param <O> the type of the operation to create
     */
    interface Constructor<O extends Operation> {
        /**
         * Creates an {@code Operation} instance from an input socket factory and an output socket factory.
         *
         * @param isf the input socket factory
         * @param osf the output socket factory
         */
        O create(InputSocket.Factory isf, OutputSocket.Factory osf);
    }

    /**
     * @return an object describing this {@code Operation}.
     */
    OperationDescription getDescription();

    default SocketsProvider getSockets() {
        return new SocketsProvider(createInputSockets(), createOutputSockets());
    }

    /**
     * @return An array of sockets for the inputs that the operation expects.
     */
    InputSocket<?>[] createInputSockets();

    /**
     * @return An array of sockets for the outputs that the operation produces.
     */
    OutputSocket<?>[] createOutputSockets();

    /**
     * Override this to provide persistent per-step data
     */
    default Optional<?> createData() {
        return Optional.empty();
    }

    /**
     * Perform the operation on the specified inputs, storing the results in the specified outputs.
     *
     * @param data Optional data to be passed to the operation
     */
    default void perform(Optional<?> data) {
        perform();
    }

    default void perform() {
        throw new UnsupportedOperationException("Operation.perform() was called but not overridden in " + getClass().getName());
    }

    /**
     * Allows the step to clean itself up when removed from the pipeline.
     * This should only be called by {@link Step#setRemoved()} to ensure correct synchronization.
     *
     * @param data Optional data to be passed to the operation
     */
    default void cleanUp(Optional<?> data) {
        /* no-op */
    }
}
