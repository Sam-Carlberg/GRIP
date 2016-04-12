package edu.wpi.grip.core.sockets;


import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.grip.core.Connection;
import edu.wpi.grip.core.GRIPCoreModule;
import edu.wpi.grip.core.MockConnection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LinkedSocketHintTest {
    private SocketHint<Boolean> booleanSocketHint;
    private SocketHint<Number> numberSocketHint;

    private InputSocket.Factory isf;
    private OutputSocket.Factory osf;

    @Before
    public void setUp() {
        booleanSocketHint = SocketHints.createBooleanSocketHint("testBooleanHint", true);
        numberSocketHint = SocketHints.createNumberSocketHint("testNumberHint", 30);
        Injector injector = Guice.createInjector(new GRIPCoreModule());
        isf = injector.getInstance(InputSocket.Factory.class);
        osf = injector.getInstance(OutputSocket.Factory.class);
    }

    @Test
    public void testConnectingAnyType() {
        final LinkedSocketHint linkedSocketHint = new LinkedSocketHint(isf, osf);

        assertTrue("Boolean should be compatible with linkedSocketHint", linkedSocketHint.isCompatibleWith(booleanSocketHint));
        assertTrue("Number should be compatible with linkedSocketHint", linkedSocketHint.isCompatibleWith(numberSocketHint));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMakingConnectionOfTypeBooleanPreventsConnectionOfTypeNumber() {
        // Given
        final EventBus eventBus = new EventBus();
        final OutputSocket booleanOutputSocket = osf.create(booleanSocketHint);
        final LinkedSocketHint linkedSocketHint = new LinkedSocketHint(isf, osf);

        // When
        final InputSocket connectedLinkedInputSocket = linkedSocketHint.linkedInputSocket("A");

        final Connection connection = new MockConnection<>(eventBus,
                (outputSocket, inputSocket) -> true,
                booleanOutputSocket, connectedLinkedInputSocket);
        connectedLinkedInputSocket.addConnection(connection);

        // Then
        assertFalse("Linked Socket Hint should no longer support number types", linkedSocketHint.isCompatibleWith(numberSocketHint));
        assertTrue("Linked Socket Hint should still accept boolean types", linkedSocketHint.isCompatibleWith(booleanSocketHint));
    }
}