/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.usecase.bis;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;
import stream.ReactStreams;
import stream.StreamId;
import stream.impl.NamedStreamId;
import stream.support.RxStreamSupport;
import stream.testing.AbstractStreamTest;

public class BisRegisterFlowTest extends AbstractStreamTest implements RxStreamSupport {

    private static final int USER_PERMIT_1_A_AND_B_TRUE = 65537;
    private static final int SOURCE_STREAM_ELEMENTS = 20;
    private static final NamedStreamId<Integer> SOURCE_ID = new NamedStreamId<>("BisUserPermitsStream");

    @Before
    public void setup() {
        //@formatter:off
        provide(Observable.from(someValues())
            .observeOn(Schedulers.newThread())
            .delay(10, TimeUnit.MILLISECONDS).limit(SOURCE_STREAM_ELEMENTS)
            .map(l -> Long.valueOf(l).intValue()))
            .as(SOURCE_ID);
        //@formatter:on
    }

    @Test
    public void test() throws InterruptedException {

        CountDownLatch sync = new CountDownLatch(1);

        //@formatter:off
        rxFrom(SOURCE_ID)
            .flatMap(this::mapBitsToUserPermits)
            .groupBy(UserPermit::getPermitId)
            .doAfterTerminate(sync::countDown)
            .subscribe(this::provideStreams);
        //@formatter:on

        sync.await();

        StreamId<Boolean> userPermit1Id = new NamedStreamId<>("");
        Observable<Boolean> userPermit1Stream = Observable.zip(rxFrom(RedundantPermitId.USER_PERMIT_1_A),
                rxFrom(RedundantPermitId.USER_PERMIT_1_B), (a, b) -> a.isGiven() & b.isGiven());
        provide(userPermit1Stream).as(userPermit1Id);

        
        rxFrom(userPermit1Id).subscribe(value -> System.out.println("User Permit 1 : " + value));
    }

    private Observable<UserPermit> mapBitsToUserPermits(Integer register) {
        List<UserPermit> bitList = new ArrayList<>(32);
        for (RedundantPermitId permitId : RedundantPermitId.values()) {
            boolean testBit = BigInteger.valueOf(register).testBit(permitId.offset);
            bitList.add(new UserPermit(permitId, testBit));
        }
        return Observable.from(bitList);
    }

    private void provideStreams(GroupedObservable<RedundantPermitId, UserPermit> groupedObservable) {
        RedundantPermitId key = groupedObservable.getKey();
        provide(ReactStreams.fromRx(groupedObservable)).as(key);
    }

    private List<Integer> someValues() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < SOURCE_STREAM_ELEMENTS; ++i) {
            values.add(i % 4 == 0 ? 0 : USER_PERMIT_1_A_AND_B_TRUE);
        }
        return values;
    }

}
