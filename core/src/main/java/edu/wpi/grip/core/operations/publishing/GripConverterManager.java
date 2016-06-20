package edu.wpi.grip.core.operations.publishing;

import edu.wpi.grip.core.operations.network.PublishAnnotatedOperation;
import edu.wpi.grip.core.operations.network.Publishable;

/**
 * Manager for GRIP-defined data types.
 */
public class GripConverterManager implements ConverterManager {

    @Override
    public void addConverters() {
        Converters.setDefaultConverter(Publishable.class, PublishAnnotatedOperation.publishableConverter);
    }
}
