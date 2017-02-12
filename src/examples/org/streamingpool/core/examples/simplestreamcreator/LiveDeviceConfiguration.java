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

import static org.streamingpool.core.examples.simplestreamcreator.LiveDeviceIds.LIVE_DEVICE_ID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.streamingpool.core.conf.StreamCreatorFactoryConfiguration;
import org.streamingpool.core.service.StreamCreator;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.impl.IdentifiedStreamCreator;
import org.streamingpool.core.service.impl.ImmutableIdentifiedStreamCreator;

/**
 * In the configuration, we link the {@link StreamCreator} with the
 * {@link StreamId}. In order to use the {@link StreamCreator} feature,
 * {@link StreamCreatorFactoryConfiguration} needs to be available in the Spring
 * context.
 *
 */
@Configuration
@Import({ StreamCreatorFactoryConfiguration.class })
public class LiveDeviceConfiguration {

	@Bean
	public IdentifiedStreamCreator<LiveDeviceReading> injectionStreamCreator() {
		return ImmutableIdentifiedStreamCreator.of(LIVE_DEVICE_ID, new LiveDeviceStreamCreator());
	}
}
