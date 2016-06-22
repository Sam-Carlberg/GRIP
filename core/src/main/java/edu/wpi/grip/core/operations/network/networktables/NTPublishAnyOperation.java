package edu.wpi.grip.core.operations.network.networktables;

import edu.wpi.grip.core.Connection;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.operations.network.MapNetworkPublisher;
import edu.wpi.grip.core.operations.network.MapNetworkPublisherFactory;
import edu.wpi.grip.core.operations.network.NetworkPublishOperation;
import edu.wpi.grip.core.operations.publishing.Converters;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.util.Icon;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Operation for publishing any type of data to Network Tables, as long as a
 * {@link edu.wpi.grip.core.operations.publishing.Converter Converter} exists
 * for it.
 */
public class NTPublishAnyOperation extends NetworkPublishOperation<Object> {

    public static final OperationDescription DESCRIPTION =
        defaultBuilder
            .name("NTPublish")
            .summary("Publishes data to a network table")
            .icon(Icon.iconStream("first"))
            .build();

    /** The type of the most recently attached data (e.g. ContoursReport, LinesReport...). */
    private Class<?> lastDataType;
    private final MapNetworkPublisherFactory publisherFactory;
    private MapNetworkPublisher<Object> publisher;

    /** The default data name. */
    private static final String DEFAULT_NAME = "myData";

    public NTPublishAnyOperation(InputSocket.Factory isf, MapNetworkPublisherFactory f) {
        super(isf, Object.class, Converters::isConvertible);
        this.publisherFactory = checkNotNull(f);
        nameSocket.setValue(DEFAULT_NAME);
    }

    @Override
    protected List<InputSocket<Boolean>> createFlagSockets() {
        // No flag sockets, everything gets published
        return ImmutableList.of();
    }

    @Override
    public void connectionAdded(Connection<?> connection) {
        if (connection.getInputSocket() != dataSocket) {
            // Use reference equality -- don't want to run this if the data socket for another
            // NTPublishAnyOperation got changed
            return;
        }
        // Change the value of the name socket if the data type has changed and
        // it's using the default name
        Class<?> type = connection.getOutputSocket().getSocketHint().getType();
        String dataName = nameSocket.getValue().get();
        if (type != lastDataType) {
            if (dataName.equals(DEFAULT_NAME) || dataName.equals("my" + lastDataType.getSimpleName())) {
                // Change name if using the default
                dataName = "my" + type.getSimpleName();
                nameSocket.setValue(dataName);
            }
            lastDataType = type;
        }
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent") // Presence is already checked by Step
    protected void doPublish() {
        final Object data = dataSocket.getValue().get();
        final String dataName = nameSocket.getValue().get();
        Map<String, Object> dataMap = Converters.convert(data);
        if (publisher == null) {
            publisher = publisherFactory.create(dataMap.keySet());
        }
        publisher.setName(dataName);
        publisher.publish(dataMap);
    }
}
