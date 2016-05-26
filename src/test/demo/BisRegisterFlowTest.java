/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import conf.SpringContext;
import rx.Observable;
import stream.ReactStreams;
import stream.StreamProcessingSupport;
import stream.impl.NamedStreamId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContext.class, loader = AnnotationConfigContextLoader.class)
public class BisRegisterFlowTest extends StreamProcessingSupport {

    private static final NamedStreamId<Integer> SOURCE_ID = new NamedStreamId<>("SourceStream");
    private static final Logger LOGGER = LoggerFactory.getLogger(BisRegisterFlowTest.class);
    private static final int SOURCE_STREAM_ELEMENTS = 20;
    private static final int SOURCE_INTERVAL_MS = 10;

    @Before
    public void setup() {
        provide(ReactStreams.fromRx(Observable.interval(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS)
                .limit(SOURCE_STREAM_ELEMENTS).map(l -> Long.valueOf(l).intValue()))).as(SOURCE_ID);
    }

    @Test
    public void test() throws InterruptedException {

        CountDownLatch sync = new CountDownLatch(1);

        ReactStreams.rxFrom(discover(SOURCE_ID)).flatMap(register -> {
            List<UserPermit> bitList = new ArrayList<>(32);
            for (PermitId permitId : PermitId.values()) {
                bitList.add(new UserPermit(permitId, BigInteger.valueOf(register).testBit(permitId.offset)));
            }
            return Observable.from(bitList);
        }).groupBy(permit -> permit.getPermitId()).doOnCompleted(sync::countDown).subscribe(groupedObservable -> {
            PermitId key = groupedObservable.getKey();

            provide(ReactStreams.fromRx(groupedObservable)).as(new NamedStreamId<>(key.toString()));

        });

        sync.await();

        Observable<UserPermit> userPermit10AStream = ReactStreams.rxFrom(discover(new NamedStreamId<>(PermitId.USER_PERMIT_1_A.toString())));
        Observable<UserPermit> userPermit10BStream = ReactStreams.rxFrom(discover(new NamedStreamId<>(PermitId.USER_PERMIT_1_B.toString())));
        
        Observable.zip(userPermit10AStream, userPermit10BStream, (userPermitA, userPermitB) -> {
            return userPermitA.isGiven() & userPermitB.isGiven();
        }).subscribe(System.out::println);
        
        
        

    }

    /**
     * Represents a user permit from the BIS
     * 
     * @author acalia
     */
    private static final class UserPermit {
        private final PermitId label;
        private final boolean value;

        /**
         * @param permitId
         * @param value
         */
        public UserPermit(PermitId permitId, boolean value) {
            super();
            this.label = permitId;
            this.value = value;
        }

        public PermitId getPermitId() {
            return label;
        }

        public boolean isGiven() {
            return value;
        }

        @Override
        public String toString() {
            return "UserPermit [label=" + label + ", value=" + value + "]";
        }
    }

    private static enum PermitId {
        USER_PERMIT_1_A(1),
        USER_PERMIT_1_B(17),
        USER_PERMIT_2_A(2),
        USER_PERMIT_2_B(18),
        USER_PERMIT_3_A(3),
        USER_PERMIT_3_B(19),
        USER_PERMIT_4_A(4),
        USER_PERMIT_4_B(20),
        USER_PERMIT_5_A(5),
        USER_PERMIT_5_B(21),
        USER_PERMIT_6_A(6),
        USER_PERMIT_6_B(22),
        USER_PERMIT_7_A(7),
        USER_PERMIT_7_B(23),
        USER_PERMIT_8_A(8),
        USER_PERMIT_8_B(24),
        USER_PERMIT_9_A(9),
        USER_PERMIT_9_B(25),
        USER_PERMIT_10_A(10),
        USER_PERMIT_10_B(26),
        USER_PERMIT_11_A(11),
        USER_PERMIT_11_B(27),
        USER_PERMIT_12_A(12),
        USER_PERMIT_12_B(28),
        USER_PERMIT_13_A(13),
        USER_PERMIT_13_B(29),
        USER_PERMIT_14_A(14),
        USER_PERMIT_14_B(30),
        USER_PERMIT_15_A(15),
        USER_PERMIT_15_B(31),
        USER_PERMIT_16_A(16),
        USER_PERMIT_16_B(32);

        private int offset;

        private PermitId(int offset) {
            this.offset = offset;
        }

    }

}
