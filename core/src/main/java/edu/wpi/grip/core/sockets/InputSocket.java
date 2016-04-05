package edu.wpi.grip.core.sockets;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import edu.wpi.grip.core.Connection;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.Source;
import edu.wpi.grip.core.Step;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the input into an {@link Operation}.
 *
 * @param <T> The type of the value that this socket stores
 */
@XStreamAlias(value = "grip:Input")
public class InputSocket<T> extends Socket<T> {

    public interface Factory {
        <T> InputSocket<T> create(SocketHint<T> hint);
    }

    public static class FactoryImpl implements Factory {

        @Inject
        private EventBus eventBus;

        @Override
        public <T> InputSocket<T> create(SocketHint<T> hint) {
            return new InputSocket<>(eventBus, hint);
        }
    }


    /**
     * @param eventBus   The Guava {@link EventBus} used by the application.
     * @param socketHint {@link #getSocketHint}
     */
    public InputSocket(EventBus eventBus, SocketHint<T> socketHint) {
        super(eventBus, socketHint, Direction.INPUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDisconnected() {
        super.onDisconnected();
        if (this.getConnections().isEmpty()) {
            this.setValue(this.getSocketHint().createInitialValue().orElse(null));
        }
    }

    public static abstract class Decorator<T> extends InputSocket<T> {

        private final InputSocket<T> decorated;

        /**
         * @param socket the decorated socket
         */
        public Decorator(InputSocket<T> socket) {
            super(socket.eventBus, socket.getSocketHint());
            this.decorated = socket;
        }

        @Override
        protected void onDisconnected() {
            decorated.onDisconnected();
        }

        @Override
        public Direction getDirection() {
            return decorated.getDirection();
        }

        @Override
        public Optional<Source> getSource() {
            return decorated.getSource();
        }

        @Override
        public Optional<Step> getStep() {
            return decorated.getStep();
        }

        @Override
        public Optional<T> getValue() {
            return decorated.getValue();
        }

        @Override
        public void addConnection(Connection connection) {
            decorated.addConnection(connection);
        }

        @Override
        public void removeConnection(Connection connection) {
            decorated.removeConnection(connection);
        }

        @Override
        public Set<Connection> getConnections() {
            return decorated.getConnections();
        }

        @Override
        public SocketHint<T> getSocketHint() {
            return decorated.getSocketHint();
        }

        @Override
        public void setSource(Optional<Source> source) {
            decorated.setSource(source);
        }

        @Override
        public void setStep(Optional<Step> step) {
            decorated.setStep(step);
        }

        @Override
        public void setValue(@Nullable T value) {
            decorated.setValue(value);
        }

        @Override
        public void setValueOptional(Optional<? extends T> optionalValue) {
            decorated.setValueOptional(optionalValue);
        }

    }

}
