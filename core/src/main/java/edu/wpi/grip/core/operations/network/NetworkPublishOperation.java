package edu.wpi.grip.core.operations.network;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icons;

/**
 * Abstract superclass for operations that publish data.
 */
public abstract class NetworkPublishOperation implements Operation {

    protected static final OperationDescription.Builder defaultBuilder =
            OperationDescription.builder()
                    .icon(Icons.iconStream("publish"))
                    .category(OperationDescription.Category.NETWORK);


    private final SocketHint<Publishable> dataHint =
            new SocketHint.Builder<>(Publishable.class)
                    .identifier("Value")
                    .build();

    private final SocketHint<String> nameHint = SocketHints.Inputs.createTextSocketHint("Name", "");

    protected final InputSocket<? extends Publishable> dataSocket;
    protected final InputSocket<String> nameSocket;

    protected NetworkPublishOperation(InputSocket.Factory isf) {
        this.dataSocket = isf.create(dataHint);
        this.nameSocket = isf.create(nameHint);
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        return new InputSocket[]{
                dataSocket,
                nameSocket
        };
    }

    @Override
    public OutputSocket<?>[] createOutputSockets() {
        return new OutputSocket[0];
    }

    /**
     * Publishes the data.
     */
    protected abstract void doPublish();

    @Override
    public void perform() {
        doPublish();
    }
}
