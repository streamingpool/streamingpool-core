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

package org.streamingpool.core.demo.projectreactor;
/*
import reactor.aeron.Context;
import reactor.aeron.publisher.AeronProcessor;
import reactor.aeron.subscriber.AeronSubscriber;
import reactor.core.publisher.Flux;
import reactor.io.buffer.Buffer;
*/
/**
 * Created by acalia on 18/05/2016.
 */
public class BasicAeronServer {
/*
    private final String serverName;
    private final String senderChannel;

    public BasicAeronServer(String serverName, String senderChannel) {
        this.serverName = serverName;
        this.senderChannel = senderChannel;
    }

    public <T> void provideStream(int streamId, Flux<T> stream) {
        Context context = Context.create()
            .name(serverName)
            .autoCancel(true)
            .streamId(streamId)
            .ringBufferSize(2)
            .serviceRequestStreamId(streamId + 3)
            .senderChannel(senderChannel)
            .receiverChannel("udp://127.0.0.1:12001");

        stream
            .map(value -> Buffer.wrap(value.toString()))
            .subscribe(AeronProcessor.create(context));
    }
*/
}
