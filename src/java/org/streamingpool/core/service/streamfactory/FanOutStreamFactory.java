package org.streamingpool.core.service.streamfactory;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import org.streamingpool.core.domain.ErrorDeflector;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.domain.backpressure.BackpressureBufferStrategy;
import org.streamingpool.core.domain.backpressure.BackpressureBufferStrategy.BackpressureBufferOverflowStrategy;
import org.streamingpool.core.domain.backpressure.BackpressureDropStrategy;
import org.streamingpool.core.domain.backpressure.BackpressureLatestStrategy;
import org.streamingpool.core.domain.backpressure.BackpressureStrategy;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.FanOutStreamId;

import static org.streamingpool.core.domain.ErrorStreamPair.ofData;

public class FanOutStreamFactory implements StreamFactory {

    private final Action NOOP = () -> {};

    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof FanOutStreamId)) {
            return ErrorStreamPair.empty();
        }

        FanOutStreamId<T> fanOutId = (FanOutStreamId<T>) id;
        BackpressureStrategy backpressureStrategy = fanOutId.backpressureStrategy();

        ErrorDeflector ed = ErrorDeflector.create();

        Flowable<T> targetStream = Flowable.fromPublisher(discoveryService.discover(fanOutId.target())).share();

        if (backpressureStrategy instanceof BackpressureLatestStrategy) {
            return ed.stream(targetStream.onBackpressureLatest());
        }
        if (backpressureStrategy instanceof BackpressureDropStrategy) {
            return ed.stream(targetStream.onBackpressureDrop(v -> System.out.println(Thread.currentThread().getName() + " Droppped " +v)));
        }
        if (backpressureStrategy instanceof BackpressureBufferStrategy) {
            BackpressureBufferStrategy bufferStrategy = (BackpressureBufferStrategy) backpressureStrategy;

            if (bufferStrategy.overflowStrategy() == BackpressureBufferOverflowStrategy.DROP_LATEST) {
                return ed.stream(targetStream.onBackpressureBuffer(bufferStrategy.bufferSize(), NOOP, BackpressureOverflowStrategy.DROP_LATEST));
            }
            if (bufferStrategy.overflowStrategy() == BackpressureBufferOverflowStrategy.DROP_OLDEST) {
                return ed.stream(targetStream.onBackpressureBuffer(bufferStrategy.bufferSize(), NOOP, BackpressureOverflowStrategy.DROP_OLDEST));
            }

            throw new IllegalArgumentException("Cannot determine the specified buffer overflow strategy: " + bufferStrategy);
        }

        throw new IllegalArgumentException("Cannot determine the specified backpressure strategy: " + backpressureStrategy);
    }
}
