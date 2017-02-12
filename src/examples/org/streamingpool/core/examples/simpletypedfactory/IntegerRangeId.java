// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.core.examples.simpletypedfactory;

import org.streamingpool.core.service.StreamId;

/**
 * This class defines the characteristics of the stream id we intend to create.
 * In this case we want a stream of integers between two specific values. NOTE:
 * it is very important that the {@link StreamId} implements a custom
 * {@link #hashCode()} and {@link #equals(Object)} methods.
 */
public class IntegerRangeId implements StreamId<Integer> {

	private final int from;
	private final int to;

	public IntegerRangeId(int from, int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + from;
		result = prime * result + to;
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
		IntegerRangeId other = (IntegerRangeId) obj;
		if (from != other.from) {
			return false;
		}
		if (to != other.to) {
			return false;
		}
		return true;
	}

}
