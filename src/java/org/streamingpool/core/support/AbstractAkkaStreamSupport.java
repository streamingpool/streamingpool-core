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

package org.streamingpool.core.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.akka.AkkaSourceProvidingService;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;

/**
 * Support class for working with Akka streams. It automatically discovers {@link AkkaSourceProvidingService} and
 * {@link ActorMaterializer} in order to fulfill the requirements of {@link AkkaStreamSupport}.
 * </p>
 * Dependency injection:
 * <ul>
 * <li>{@link AkkaSourceProvidingService}</li>
 * <li>{@link ActorMaterializer}</li>
 * </ul>
 * 
 * @author kfuchsbe
 */
public abstract class AbstractAkkaStreamSupport extends AbstractStreamSupport implements AkkaStreamSupport {

    @Autowired
    private AkkaSourceProvidingService sourceProvidingService;
    @Autowired
    private ActorMaterializer materializer;

    @Override
    public Materializer materializer() {
        return materializer;
    }

    @Override
    public AkkaSourceProvidingService sourceProvidingService() {
        return sourceProvidingService;
    }

}