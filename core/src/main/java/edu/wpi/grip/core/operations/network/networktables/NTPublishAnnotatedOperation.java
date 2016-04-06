package edu.wpi.grip.core.operations.network.networktables;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.operations.network.PublishAnnotatedOperation;
import edu.wpi.grip.core.operations.network.PublishValue;
import edu.wpi.grip.core.operations.network.Publishable;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.util.Icons;
import org.apache.commons.lang3.tuple.Pair;

/**
 * An operation that publishes any type that implements {@link Publishable} to NetworkTables.
 * <p>
 * To be publishable, a type should have one or more accessor methods annotated with {@link PublishValue}.  This is done
 * with annotations instead of methods
 */
public class NTPublishAnnotatedOperation extends PublishAnnotatedOperation {

    public NTPublishAnnotatedOperation(InputSocket.Factory inputSocketFactory) {
        super(inputSocketFactory);
    }

    @Override
    public OperationDescription getDescription() {
        return defaultBuilder
                .constructor((i, o) -> new NTPublishAnnotatedOperation(i))
                .name("NTPublish")
                .description("Publishes data to a network table")
                .icon(Icons.iconStream("first"))
                .build();
    }

    @Override
    protected void doPublish() {
        valueMethodStream()
                .map(m -> Pair.of(m.getAnnotation(PublishValue.class).key(), get(m, dataSocket.getValue().get())))
                .forEach(pair -> {
                    // lazy
                    // TODO use NTManager for publishing stuff
                    NetworkTable.getTable("GRIP").getSubTable(nameSocket.getValue().get()).putValue(pair.getLeft(), pair.getRight());
                });
    }
}
