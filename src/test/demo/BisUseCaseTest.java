package demo;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;
import stream.impl.NamedStreamId;
import stream.impl.SimplePool;

/**
 * Created by garnierj on 20/05/2016.
 */
public class BisUseCaseTest {

    @Test
    public void test() throws IOException, InterruptedException {
        System.out.println("Here it begins");
        SimplePool streamPool = new SimplePool();

        StreamId<Boolean> bisBeamPermitId = new NamedStreamId<>("bisBeamPermit");
        rx.Observable<Boolean> rxBisBeamPermitStream = rx.Observable.fromCallable(() -> {
                TimeUnit.SECONDS.sleep(3);

                return new Random().nextBoolean();
        });

        ReactStream bisBeamPermit = ReactStreams.fromRx(rxBisBeamPermitStream);
        streamPool.provide(bisBeamPermitId, bisBeamPermit);

        ReactStream<Boolean> bisBeamPermitStream = streamPool.discover(bisBeamPermitId);
        Observable<Boolean> discoveredRxStream = ReactStreams.rxFrom(bisBeamPermitStream);
        discoveredRxStream.map(beamPermit -> beamPermit?"Given":"interlocked").forEach(x-> stuff(x));

        System.out.println("Here it ends");
        TimeUnit.SECONDS.sleep(3);
    }

    private void stuff(String x) {
        System.out.println(x);
    }
}
