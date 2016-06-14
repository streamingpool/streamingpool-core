/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import static akka.stream.ThrottleMode.shaping;
import static cern.streaming.pool.core.util.UncheckedWaits.waitFor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import akka.NotUsed;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.support.StreamCollectingSupport;
import cern.streaming.pool.core.service.test.AbstractAkkaStreamTest;
import cern.streaming.pool.core.util.ReactStreams;
import scala.concurrent.duration.Duration;

public class AkkaSourceProvidingTest extends AbstractAkkaStreamTest implements StreamCollectingSupport {

	private static final Source<Integer, NotUsed> COUNTER_50_HZ = Source.range(1, 100)
			.throttle(50, Duration.create(1, SECONDS), 1, shaping()).buffer(1, OverflowStrategy.dropBuffer());

	private static final StreamId<Integer> STREAM_ID = ReactStreams.namedId("ticker");

	@BeforeClass
	public static final void setUpBeforeClass() {
		BasicConfigurator.configure();
	}

	@Ignore
	@Test
	public void provideMaterializedIsRunningFromTheBeginning() {
		provide(COUNTER_50_HZ).materialized().as(STREAM_ID);

		waitFor(1500, MILLISECONDS);

		/*
		 * The first emitted observable is always 1; It seems that this comes
		 * from the input buffer of the Sink when transformed to a publisher.
		 */
		assertThat(firstEmittedItem()).isEqualTo(1);

		/*
		 * The second observed value should be higher than 50 after the wait,
		 * because the source starts ticking on registration (=materialization)
		 */
		assertThat(secondEmittedItem()).isGreaterThanOrEqualTo(50);
	}

	@Ignore
	@Test
	public void provideUnmaterializedStartsRunningOnLookup() {
		provide(COUNTER_50_HZ).as(STREAM_ID);

		waitFor(1500, MILLISECONDS);

		/*
		 * The first emitted observable is always 1; It seems that this comes
		 * from the input buffer of the Sink when transformed to a publisher.
		 */
		assertThat(firstEmittedItem()).isEqualTo(1);

		/*
		 * The second observed value should be quite small (we assume <5) even
		 * after a wait, because the source starts ticking on first lookup.
		 */
		assertThat(secondEmittedItem()).isLessThan(5);
	}

	@Test
	public void subscribeTwiceOnMaterializedProvidedIsPossible() {
		provide(COUNTER_50_HZ).materialized().as(STREAM_ID);
		assertSubscribeTwiceIsPossible();
	}

	@Test
	public void subscribeTwiceOnUnmaterializedProvidedIsPossible() {
		provide(COUNTER_50_HZ).as(STREAM_ID);
		assertSubscribeTwiceIsPossible();
	}

	private Integer secondEmittedItem() {
		return from(STREAM_ID).skip(1).and().awaitNext();
	}

	private Integer firstEmittedItem() {
		return from(STREAM_ID).awaitNext();
	}

	private void assertSubscribeTwiceIsPossible() {
		rxFrom(STREAM_ID).subscribe((val) -> System.out.println("A:" + val));
		/*
		 * This call would throw, if the source would not have been materialized
		 * with a sink with fanout
		 */
		rxFrom(STREAM_ID).toBlocking().first();
	}

}
