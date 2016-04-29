package edu.wpi.grip.core.operations.opencv.templated;


import com.google.common.collect.ImmutableList;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;

import java.util.List;

/**
 * An Operation that takes two input values and produces one output value
 * @param <T1>
 * @param <T2>
 * @param <R>
 */
public class TwoSourceOneDestinationOperation<T1, T2, R> implements Operation {
    private final InputSocket<T1> input1;
    private final InputSocket<T2> input2;
    private final OutputSocket<R> output;
    private final Performer<T1, T2, R> performer;

    @FunctionalInterface
    public interface Performer<T1, T2, R> {
        void perform(T1 src1, T2 src2, R dst);
    }

    public TwoSourceOneDestinationOperation(
            InputSocket.Factory inputSocketFactory,
            OutputSocket.Factory outputSocketFactory,
            Performer<T1, T2, R> performer,
            SocketHint<T1> t1SocketHint,
            SocketHint<T2> t2SocketHint,
            SocketHint<R> rSocketHint) {
        this.performer = performer;
        this.input1 = inputSocketFactory.create(t1SocketHint);
        this.input2 = inputSocketFactory.create(t2SocketHint);
        this.output = outputSocketFactory.create(rSocketHint);
    }


    public TwoSourceOneDestinationOperation(
            InputSocket.Factory inputSocketFactory,
            OutputSocket.Factory outputSocketFactory,
            Performer<T1, T2, R> performer,
            Class<T1> t1, Class<T2> t2, Class<R> r) {
        this(
                inputSocketFactory,
                outputSocketFactory,
                performer,
                new SocketHint.Builder<>(t1).identifier("src1").build(),
                new SocketHint.Builder<>(t2).identifier("src2").build(),
                new SocketHint.Builder<>(r).identifier("dst").build());


    }

    @Override
    public OperationDescription getDescription() {
        return null;
    }

    @Override
    public List<InputSocket> getInputSockets() {
        return ImmutableList.of(input1, input2);
    }

    @Override
    public List<OutputSocket> getOutputSockets() {
        return ImmutableList.of(output);
    }

    @Override
    public void perform() {
        performer.perform(input1.getValue().get(), input2.getValue().get(), output.getValue().get());
        output.setValue(output.getValue().get());
    }
}
