/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.demo.projectreactor;
/*
import org.reactivestreams.Subscription;
import reactor.aeron.Context;
import reactor.aeron.publisher.AeronFlux;
import reactor.core.publisher.Flux;
import reactor.core.subscriber.BaseSubscriber;
import reactor.io.buffer.Buffer;
*/
public class BasicAeronClient {

    private final String publisherName;
    private final String senderChannel;
    private final String receiverChannel;

    public BasicAeronClient(String publisherName, String senderChannel, String receiverChannel) {
        this.publisherName = publisherName;
        this.senderChannel = senderChannel;
        this.receiverChannel = receiverChannel;
    }
/*
    public Flux<String> discover(int streamId) {
        Context context = Context.create().name(publisherName).autoCancel(true).ringBufferSize(2).streamId(streamId)
                .serviceRequestStreamId(streamId + 3).receiverChannel(receiverChannel).senderChannel(senderChannel);

        return AeronFlux.listenOn(context).as(Buffer::bufferToString);
    }
*/
}