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

package org.streamingpool.core.examples.simplestreamcreator;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.testing.NamedStreamId;

/**
 * This class just holds the constants of the {@link StreamId}s.
 *
 */
public final class LiveDeviceIds {

	private LiveDeviceIds() {
	}

	public static final StreamId<LiveDeviceReading> LIVE_DEVICE_ID = NamedStreamId
			.ofName("Any live device");
}
