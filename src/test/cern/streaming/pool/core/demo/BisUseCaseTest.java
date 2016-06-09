package cern.streaming.pool.core.demo;

import java.util.Random;

import org.junit.Test;

import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.NamedStreamId;
import cern.streaming.pool.core.service.impl.SimplePool;
import cern.streaming.pool.core.util.ReactStreams;
import rx.Observable;

/**
 * Created by garnierj on 20/05/2016.
 */
public class BisUseCaseTest {

    @Test
    /* (KF) removed sleeps for the moment */
    public void doNotKnowWhatThisTestDoes() {
        System.out.println("Here it begins");
        SimplePool streamPool = new SimplePool();

        StreamId<Boolean> bisBeamPermitId = new NamedStreamId<>("bisBeamPermit");
        rx.Observable<Boolean> rxBisBeamPermitStream = rx.Observable.fromCallable(() -> {
            // TimeUnit.SECONDS.sleep(3);

            return new Random().nextBoolean();
        });

        ReactStream<Boolean> bisBeamPermit = ReactStreams.fromRx(rxBisBeamPermitStream);
        streamPool.provide(bisBeamPermitId, bisBeamPermit);

        ReactStream<Boolean> bisBeamPermitStream = streamPool.discover(bisBeamPermitId);
        Observable<Boolean> discoveredRxStream = ReactStreams.rxFrom(bisBeamPermitStream);
        discoveredRxStream.map(beamPermit -> beamPermit ? "Given" : "interlocked").forEach(x -> print(x));

        System.out.println("Here it ends");
        // TimeUnit.SECONDS.sleep(3);
    }

    private void print(String x) {
       System.out.println(x);
    }
}
