package cern.streaming.pool.core.demo;

import java.util.Random;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.LocalPool;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import cern.streaming.pool.core.testing.NamedStreamId;
import rx.Observable;

/**
 * Created by garnierj on 20/05/2016.
 */
public class TryBisUseCase {

    public static void main(String[] args) {
        System.out.println("Here it begins");
        LocalPool streamPool = new LocalPool();

        StreamId<Boolean> bisBeamPermitId = new NamedStreamId<>("bisBeamPermit");
        rx.Observable<Boolean> rxBisBeamPermitStream = rx.Observable.fromCallable(() -> {
            // TimeUnit.SECONDS.sleep(3);

            return new Random().nextBoolean();
        });

        ReactiveStream<Boolean> bisBeamPermit = ReactiveStreams.fromRx(rxBisBeamPermitStream);
        streamPool.provide(bisBeamPermitId, bisBeamPermit);

        ReactiveStream<Boolean> bisBeamPermitStream = streamPool.discover(bisBeamPermitId);
        Observable<Boolean> discoveredRxStream = ReactiveStreams.rxFrom(bisBeamPermitStream);
        discoveredRxStream.map(beamPermit -> beamPermit ? "Given" : "interlocked").forEach(x -> print(x));

        System.out.println("Here it ends");
        // TimeUnit.SECONDS.sleep(3);
    }

    private static void print(String x) {
       System.out.println(x);
    }
}
