package edu.wpi.grip.core.operations;


import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.grip.core.GRIPCoreModule;
import edu.wpi.grip.core.operations.network.MapNetworkPublisherFactory;
import edu.wpi.grip.core.operations.network.MockMapNetworkPublisher;
import edu.wpi.grip.core.operations.network.ros.JavaToMessageConverter;
import edu.wpi.grip.core.operations.network.ros.ROSMessagePublisher;
import edu.wpi.grip.core.operations.network.ros.ROSNetworkPublisherFactory;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;

import java.util.Optional;

public class OperationsFactory {

    private static class MockROSMessagePublisher<C extends JavaToMessageConverter> extends ROSMessagePublisher {
        public MockROSMessagePublisher(C converter) {

        }

        @Override
        public void publish(ROSMessagePublisher.Converter publish) {

        }

        @Override
        protected void publishNameChanged(Optional<String> oldName, String newName) {

        }

        @Override
        public void close() {

        }
    }

    public static Operations create(EventBus eventBus) {
        Injector injector = Guice.createInjector(new GRIPCoreModule());
        InputSocket.Factory isf = injector.getInstance(InputSocket.Factory.class);
        OutputSocket.Factory osf = injector.getInstance(OutputSocket.Factory.class);
        return create(eventBus, MockMapNetworkPublisher::new, MockROSMessagePublisher::new, isf, osf);
    }

    public static Operations create(EventBus eventBus,
                                    MapNetworkPublisherFactory mapFactory,
                                    ROSNetworkPublisherFactory rosFactory,
                                    InputSocket.Factory isf,
                                    OutputSocket.Factory osf) {
        return new Operations(eventBus, mapFactory, rosFactory, isf, osf);
    }

    public static CVOperations createCV(EventBus eventBus) {
        Injector injector = Guice.createInjector(new GRIPCoreModule());
        InputSocket.Factory isf = injector.getInstance(InputSocket.Factory.class);
        OutputSocket.Factory osf = injector.getInstance(OutputSocket.Factory.class);
        return new CVOperations(eventBus, isf, osf);
    }
}
