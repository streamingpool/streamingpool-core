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
import org.reactivestreams.Subscription;
import reactor.aeron.Context;
import reactor.aeron.publisher.AeronFlux;
import reactor.core.publisher.Flux;
import reactor.core.subscriber.BaseSubscriber;
import reactor.io.buffer.Buffer;
*/
public class BasicAeronClient {

    /*
    private final String publisherName;
    private final String senderChannel;
    private final String receiverChannel;

    public BasicAeronClient(String publisherName, String senderChannel, String receiverChannel) {
        this.publisherName = publisherName;
        this.senderChannel = senderChannel;
        this.receiverChannel = receiverChannel;
    }
    public Flux<String> discover(int streamId) {
        Context context = Context.create().name(publisherName).autoCancel(true).ringBufferSize(2).streamId(streamId)
                .serviceRequestStreamId(streamId + 3).receiverChannel(receiverChannel).senderChannel(senderChannel);

        return AeronFlux.listenOn(context).as(Buffer::bufferToString);
    }
*/
}