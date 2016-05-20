package demo;

import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;
import stream.impl.NamedStreamId;
import stream.impl.SimplePool;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by garnierj on 20/05/2016.
 */
public class BisUseCaseTest {

    @Test
    public void test() throws IOException, InterruptedException {
        System.out.println("Here is begins");
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

        System.out.println("Here is ends");
        TimeUnit.SECONDS.sleep(100);
    }

    private void stuff(String x) {
        System.out.println(x);
    }
}
