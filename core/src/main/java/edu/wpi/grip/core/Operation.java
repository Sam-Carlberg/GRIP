package edu.wpi.grip.core;

import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketsProvider;

/**
 * The common interface used by <code>Step</code>s in a pipeline to call various operations.  There is usually only one
 * instance of any class that implements <code>Operation</code>, which is called whenever that operation is used.
 */
public interface Operation {

    /**
     * @return an object describing this {@code Operation}.
     */
    OperationDescription getDescription();

    default SocketsProvider getSockets() {
        return new SocketsProvider(getInputSockets(), getOutputSockets());
    }

    /**
     * @return An array of sockets for the inputs that the operation expects.
     */
    InputSocket<?>[] getInputSockets();

    /**
     * @return An array of sockets for the outputs that the operation produces.
     */
    OutputSocket<?>[] getOutputSockets();

    /**
     * Performs this {@code Operation}.
     */
    void perform();

    /**
     * Allows the step to clean itself up when removed from the pipeline.
     * This should only be called by {@link Step#setRemoved()} to ensure correct synchronization.
     */
    default void cleanUp() {
        /* no-op */
    }
}
