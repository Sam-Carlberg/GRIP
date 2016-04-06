package edu.wpi.grip.core.operations;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.events.OperationAddedEvent;
import edu.wpi.grip.core.operations.composite.BlurOperation;
import edu.wpi.grip.core.operations.composite.ConvexHullsOperation;
import edu.wpi.grip.core.operations.composite.DesaturateOperation;
import edu.wpi.grip.core.operations.composite.DistanceTransformOperation;
import edu.wpi.grip.core.operations.composite.FilterContoursOperation;
import edu.wpi.grip.core.operations.composite.FilterLinesOperation;
import edu.wpi.grip.core.operations.composite.FindBlobsOperation;
import edu.wpi.grip.core.operations.composite.FindContoursOperation;
import edu.wpi.grip.core.operations.composite.FindLinesOperation;
import edu.wpi.grip.core.operations.composite.HSLThresholdOperation;
import edu.wpi.grip.core.operations.composite.HSVThresholdOperation;
import edu.wpi.grip.core.operations.composite.MaskOperation;
import edu.wpi.grip.core.operations.composite.NormalizeOperation;
import edu.wpi.grip.core.operations.composite.PublishVideoOperation;
import edu.wpi.grip.core.operations.composite.RGBThresholdOperation;
import edu.wpi.grip.core.operations.composite.ResizeOperation;
import edu.wpi.grip.core.operations.composite.SwitchOperation;
import edu.wpi.grip.core.operations.composite.ValveOperation;
import edu.wpi.grip.core.operations.composite.WatershedOperation;
import edu.wpi.grip.core.operations.network.MapNetworkPublisherFactory;
import edu.wpi.grip.core.operations.network.ros.ROSNetworkPublisherFactory;
import edu.wpi.grip.core.operations.opencv.MatFieldAccessor;
import edu.wpi.grip.core.operations.opencv.MinMaxLoc;
import edu.wpi.grip.core.operations.opencv.NewPointOperation;
import edu.wpi.grip.core.operations.opencv.NewSizeOperation;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class Operations {
    private final EventBus eventBus;
    private final ImmutableList<OperationDescription> operations;

    @Inject
    Operations(EventBus eventBus,
               @Named("ntManager") MapNetworkPublisherFactory ntPublisherFactory,
               @Named("rosManager") ROSNetworkPublisherFactory rosPublishFactory,
               InputSocket.Factory isf,
               OutputSocket.Factory osf) {
        this.eventBus = checkNotNull(eventBus, "EventBus cannot be null");
        checkNotNull(ntPublisherFactory, "ntPublisherFactory cannot be null");
        checkNotNull(rosPublishFactory, "rosPublishFactory cannot be null");
        this.operations = ImmutableList.of(
                BlurOperation.DESCRIPTION,
                ConvexHullsOperation.DESCRIPTION,
                DesaturateOperation.DESCRIPTION,
                DistanceTransformOperation.DESCRIPTION,
                FilterContoursOperation.DESCRIPTION,
                FilterLinesOperation.DESCRIPTION,
                FindBlobsOperation.DESCRIPTION,
                FindContoursOperation.DESCRIPTION,
                FindLinesOperation.DESCRIPTION,
                HSLThresholdOperation.DESCRIPTION,
                HSVThresholdOperation.DESCRIPTION,
                MaskOperation.DESCRIPTION,
                NormalizeOperation.DESCRIPTION,
                PublishVideoOperation.DESCRIPTION,
                ResizeOperation.DESCRIPTION,
                RGBThresholdOperation.DESCRIPTION,
                SwitchOperation.DESCRIPTION,
                ValveOperation.DESCRIPTION,
                WatershedOperation.DESCRIPTION,

                MatFieldAccessor.DESCRIPTION,
                MinMaxLoc.DESCRIPTION,
                NewPointOperation.DESCRIPTION,
                NewSizeOperation.DESCRIPTION
        );
    }

    public void addOperations() {
        operations.stream()
                .map(OperationAddedEvent::new)
                .forEach(eventBus::post);
    }
}
