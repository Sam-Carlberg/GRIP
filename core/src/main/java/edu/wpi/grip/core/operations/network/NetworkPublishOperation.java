package edu.wpi.grip.core.operations.network;

import com.google.common.reflect.TypeToken;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.sockets.SocketHints;
import edu.wpi.grip.core.util.Icons;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass for operations that publish data.
 */
public abstract class NetworkPublishOperation<D> implements Operation {

    /**
     * OperationDescription builder that has the icon default to "publish" and the category to "NETWORK".
     */
    protected static final OperationDescription.Builder defaultBuilder =
            OperationDescription.builder()
                    .icon(Icons.iconStream("publish"))
                    .category(OperationDescription.Category.NETWORK);


    protected final Class<D> dataType;

    private final SocketHint<D> dataHint = (SocketHint<D>)
            new SocketHint.Builder<>(new TypeToken<D>(getClass()){}.getRawType())
                    .identifier("Data")
                    .build();
    private final SocketHint<String> nameHint = SocketHints.Inputs.createTextSocketHint("Name", "");

    protected final InputSocket<D> dataSocket;
    protected final InputSocket<String> nameSocket;

    protected NetworkPublishOperation(InputSocket.Factory isf, Class<D> dataType) {
        this.dataType = dataType;
        this.dataSocket = isf.create(dataHint);
        this.nameSocket = isf.create(nameHint);
    }

    @Override
    public InputSocket<?>[] createInputSockets() {
        List<InputSocket<?>> sockets = new ArrayList<>();
        sockets.add(dataSocket);
        sockets.add(nameSocket);
        sockets.addAll(createFlagSockets());
        return sockets.toArray(new InputSocket[0]);
    }

    /**
     * Creates a list of input sockets that control which items to publish.
     */
    protected abstract List<InputSocket<Boolean>> createFlagSockets();

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
