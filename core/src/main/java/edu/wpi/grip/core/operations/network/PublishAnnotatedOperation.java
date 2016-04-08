package edu.wpi.grip.core.operations.network;

import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.SocketHints;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Publishes data to a specific network protocol.
 * <p>
 * This looks at {@link PublishValue} annotations on accessor methods in a class to generate the data to publish.
 */
public abstract class PublishAnnotatedOperation<D, P extends Publishable> extends NetworkPublishOperation<D> {

    protected final InputSocket.Factory isf;
    protected final Class<P> publishType;
    protected final Function<D, P> converter;

    protected PublishAnnotatedOperation(InputSocket.Factory isf,
                                        Class<D> dataType,
                                        Class<P> publishType,
                                        Function<D, P> converter) {
        super(isf, dataType);
        this.isf = isf;
        this.publishType = publishType;
        this.converter = converter;
    }

    /**
     * Gets a stream of all valid methods annotated with {@link PublishValue} in the class of the data to publish.
     * The methods are sorted by weight.
     */
    protected Stream<Method> valueMethodStream() {
        return Stream.of(publishType.getMethods())
                .filter(m -> m.isAnnotationPresent(PublishValue.class))
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .sorted(Comparator.comparing(m -> m.getAnnotation(PublishValue.class).weight()));
    }

    @Override
    protected List<InputSocket<Boolean>> createFlagSockets() {
        return valueMethodStream()
                .map(m -> m.getAnnotation(PublishValue.class).key())
                .map(name -> SocketHints.createBooleanSocketHint("Publish " + name, true))
                .map(isf::create)
                .collect(Collectors.toList());
    }

    /**
     * Helper method for invoking an accessor method on an object.
     *
     * @param accessor the accessor method to invoke
     * @param instance the object to invoke the accessor on
     * @return the value returned by the accessor method, or {@code null} if the method could not be invoked.
     */
    protected Object get(Method accessor, Object instance) {
        try {
            return accessor.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

}
