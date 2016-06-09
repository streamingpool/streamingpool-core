/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.support;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.util.RxStreams;

public interface StreamCollectingSupport extends RxStreamSupport {

	default <T> OngoingBlockingCollecting<T> from(StreamId<T> streamId) {
		return new OngoingBlockingCollecting<>(streamId, this);
	}

	public static class OngoingBlockingCollecting<T> {
		private int skip = 0;
		private final StreamId<T> streamId;
		private final RxStreamSupport support;

		public OngoingBlockingCollecting(StreamId<T> streamId, RxStreamSupport support) {
			this.support = requireNonNull(support, "RxStreamSupport must not be null");
			this.streamId = requireNonNull(streamId, "streamId must not be null");
			if (skip < 0) {
				throw new IllegalArgumentException(
						"The number of acquisitions to skip must be >=0, but was " + skip + ".");
			}
		}

		public OngoingBlockingCollecting<T> skip(int itemsToSkip) {
			this.skip = itemsToSkip;
			return this;
		}

		public OngoingBlockingCollecting<T> and() {
			return this;
		}

		public T awaitNext() {
			return RxStreams.awaitNext(support.rxFrom(streamId).skip(skip));
		}

	}

}