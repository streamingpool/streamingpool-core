/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.demo.usecase.bis;

import static cern.streaming.pool.core.demo.usecase.bis.RedundantPermitId.USER_PERMIT_1_A;
import static cern.streaming.pool.core.demo.usecase.bis.RedundantPermitId.USER_PERMIT_1_B;
import static io.reactivex.Flowable.fromIterable;
import static io.reactivex.Flowable.fromPublisher;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.support.RxStreamSupport;
import cern.streaming.pool.core.testing.AbstractStreamTest;
import cern.streaming.pool.core.testing.NamedStreamId;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;
import io.reactivex.Flowable;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.schedulers.Schedulers;

public class BisRegisterFlowTest extends AbstractStreamTest implements RxStreamSupport {

    private static final int USER_PERMIT_1_A_AND_B_TRUE = 65537;
    private static final int SOURCE_STREAM_ELEMENTS = 20;
    private static final NamedStreamId<Integer> SOURCE_ID = new NamedStreamId<>("BisUserPermitsStream");
    private static final List<Integer> DEMO_VALUES = someValues();

    @Before
    public void setup() {
        // @formatter:off
        provide(fromIterable(DEMO_VALUES)
		        .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
				.delay(10, TimeUnit.MILLISECONDS)
				.take(SOURCE_STREAM_ELEMENTS)
				.map(l -> Long.valueOf(l).intValue()))
				.as(SOURCE_ID);
		// @formatter:on
    }

    @Test
    public void testRxJava2() throws InterruptedException {
        CountDownLatch sync = new CountDownLatch(1);

        // rxFrom(SOURCE_ID).flatMap(this::mapBitsToUserPermits).groupBy(UserPermit::getPermitId)
        // .doOnTerminate(sync::countDown).subscribe(System.out::println);

        Flowable.just(Arrays.asList(1, 2), Arrays.asList(3, 4)).flatMap(Flowable::fromIterable).groupBy(value -> value % 2 == 0)
                .doOnTerminate(sync::countDown).subscribe(System.out::println);

        sync.await();
    }

    @Ignore
    @Test
    public void test() throws InterruptedException {

        CountDownLatch sync = new CountDownLatch(1);

        // @formatter:off
		rxFrom(SOURCE_ID)
		    .flatMap(this::mapBitsToUserPermits)
		    .groupBy(UserPermit::getPermitId)
			.doOnTerminate(sync::countDown)
			.subscribe(this::provideStreams);
		// @formatter:on

        sync.await();

        StreamId<Boolean> userPermit1Id = new NamedStreamId<>("");
        Flowable<Boolean> userPermit1Stream = Flowable.zip(fromPublisher(discover(USER_PERMIT_1_A)),
                fromPublisher(discover(USER_PERMIT_1_B)), (a, b) -> a.isGiven() & b.isGiven());
        provide(userPermit1Stream).as(userPermit1Id);

        BlockingTestSubscriber<Boolean> subscriber = BlockingTestSubscriber.ofName("subscriber");
        discover(userPermit1Id).subscribe(subscriber);

        subscriber.await();

        assertThat(subscriber.getValues()).hasSize(SOURCE_STREAM_ELEMENTS).contains(true, false);
    }

    private Flowable<UserPermit> mapBitsToUserPermits(Integer register) {
        List<UserPermit> bitList = new ArrayList<>(32);
        for (RedundantPermitId permitId : RedundantPermitId.values()) {
            boolean testBit = BigInteger.valueOf(register).testBit(permitId.offset);
            bitList.add(new UserPermit(permitId, testBit));
        }
        return Flowable.fromIterable(bitList);
    }

    private void provideStreams(GroupedFlowable<RedundantPermitId, UserPermit> groupedObservable) {
        RedundantPermitId key = groupedObservable.getKey();
        provide(groupedObservable).as(key);
    }

    private static List<Integer> someValues() {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < SOURCE_STREAM_ELEMENTS; ++i) {
            values.add(i % 4 == 0 ? 0 : USER_PERMIT_1_A_AND_B_TRUE);
        }
        return values;
    }

}
