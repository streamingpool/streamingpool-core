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

package org.streamingpool.core.testing;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.streamingpool.core.conf.AkkaStreamingConfiguration;
import org.streamingpool.core.conf.DefaultStreamFactories;
import org.streamingpool.core.conf.EmbeddedPoolConfiguration;
import org.streamingpool.core.conf.StreamCreatorFactoryConfiguration;
import org.streamingpool.core.support.AbstractAkkaStreamSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class, AkkaStreamingConfiguration.class,
        DefaultStreamFactories.class,
        StreamCreatorFactoryConfiguration.class }, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public abstract class AbstractAkkaStreamTest extends AbstractAkkaStreamSupport {
    /* Nothing to do here. Only the contexts and the test runner */
}
