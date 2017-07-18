/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.incubation.akka;

import java.util.Objects;
import java.util.Optional;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamId;

import akka.NotUsed;
import akka.stream.Attributes;
import akka.stream.Outlet;
import akka.stream.SourceShape;
import akka.stream.scaladsl.Source;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

public class StreamIdSource<T> extends GraphStage<SourceShape<T>> {

    private final StreamId<T> streamId;
    private final Outlet<T> out = Outlet.create("StreamIdSource.out");
    private final SourceShape<T> shape = SourceShape.of(out);

    public StreamIdSource(StreamId<T> streamId) {
        this.streamId = Objects.requireNonNull(streamId, "streamId must not be null");
    }

    public static final <T> StreamIdSource<T> fromStreamId(StreamId<T> id) {
        return new StreamIdSource<>(id);
    }

    @Override
    public SourceShape<T> shape() {
        return shape;
    }

    @Override
    public GraphStageLogic createLogic(Attributes arg0) throws Exception {

        akka.stream.scaladsl.Source<T, NotUsed> delegate = getDelegatingSource(arg0);
        
        
        return new GraphStageLogic(shape) {
            {
                setHandler(out, new AbstractOutHandler() {

                    @Override
                    public void onPull() throws Exception {
                        T elem = null;
                        push(out, elem);
                    }
                });
            }
        };
    }

    private akka.stream.scaladsl.Source<T, NotUsed> getDelegatingSource(Attributes inheritedAttributes) {
        DiscoveryService service = getDiscoveryService(inheritedAttributes);
        Publisher<T> publisher = service.discover(streamId);
        return Source.fromPublisher(publisher);
    }

    // from attributes? injected?
    // currently package protected for testing purposes
    protected DiscoveryService getDiscoveryService(Attributes inheritedAttributes) {
        Optional<StreamDiscovery> discovery = inheritedAttributes.getAttribute(StreamDiscovery.class);
        return discovery.get().service();
    }

}
