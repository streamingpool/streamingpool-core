package org.streamingpool.core.service.diagnostic;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import org.streamingpool.core.service.StreamId;

public class ErrorStreamId<S extends StreamId<?>> implements StreamId<Throwable>, Serializable {
    private static final long serialVersionUID = 1L;

    private final S sourceId;

	private ErrorStreamId(S sourceId) {
		this.sourceId = requireNonNull(sourceId, "sourceId must not be null");
	}

	public static <S extends StreamId<?>> ErrorStreamId<S> of(S sourceId) {
        return new ErrorStreamId<>(sourceId);
    }

	public S sourceId() {
		return sourceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ErrorStreamId<?> other = (ErrorStreamId<?>) obj;
		if (sourceId == null) {
			if (other.sourceId != null) {
				return false;
			}
		} else if (!sourceId.equals(other.sourceId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ErrorStreamId [sourceId=" + sourceId + "]";
	}

}
