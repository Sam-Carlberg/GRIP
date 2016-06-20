package edu.wpi.grip.core.operations.network.networktables;

import edu.wpi.grip.core.operations.network.MapNetworkPublisher;
import edu.wpi.grip.core.operations.network.MapNetworkPublisherFactory;
import edu.wpi.grip.core.operations.network.NetworkPublishOperation;
import edu.wpi.grip.core.operations.publishing.Converters;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.SocketHints;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Operation for publishing any type of data to Network Tables.
 */
public class NTPublishAnyOperation extends NetworkPublishOperation<Object> {

    private final InputSocket.Factory isf;
    private final MapNetworkPublisherFactory publisherFactory;
    private MapNetworkPublisher<Object> publisher;
    private Map<String, Object> dataMap;

    public NTPublishAnyOperation(InputSocket.Factory isf, MapNetworkPublisherFactory f) {
        super(isf, Object.class);
        this.isf = isf;
        this.publisherFactory = f;
    }

    @Override
    protected List<InputSocket<Boolean>> createFlagSockets() {
        if (dataMap == null) {
            System.out.println("Data map is null, returning empty list for sockets");
            return ImmutableList.of();
        } else {
            System.out.println("Creating sockets from data map");
            return dataMap.keySet().stream()
                .map(field -> isf.create(SocketHints.createBooleanSocketHint(field, true)))
                .collect(Collectors.toList());
        }
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent") // Presence is already checked by Step
    protected void doPublish() {
        final Object data = dataSocket.getValue().get();
        final String dataName = nameSocket.getValue().get();
        dataMap = Converters.convert(data);
        if (publisher == null) {
            publisher = publisherFactory.create(dataMap.keySet());
        }
        publisher.setName(dataName);
        publisher.publish(dataMap);
    }
}
