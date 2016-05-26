/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.projectreactor;
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
