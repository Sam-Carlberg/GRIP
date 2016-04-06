package edu.wpi.grip.core.operations.network;

import edu.wpi.grip.core.sockets.InputSocket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Publishes data to a specific network protocol.
 */
public abstract class PublishAnnotatedOperation extends NetworkPublishOperation {

    protected PublishAnnotatedOperation(InputSocket.Factory isf) {
        super(isf);
    }

    private boolean isDataPresent() {
        return dataSocket.getValue().isPresent();
    }

    protected Stream<Method> valueMethodStream() {
        if (!isDataPresent()) {
            return Stream.empty();
        }
        return Stream.of(dataSocket.getValue().get().getClass().getMethods())
                .filter(m -> m.isAnnotationPresent(PublishValue.class))
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .sorted(Comparator.comparing(m -> m.getAnnotation(PublishValue.class).weight()));
    }

    protected Object get(Method getter, Object instance) {
        try {
            return getter.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

}
