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
package cern.streaming.pool.core.incubation.akka;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import akka.stream.Attributes;
import akka.stream.Outlet;
import akka.stream.SourceShape;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamId;

/**
 * Created by mgalilee on 26/05/2016.
 */
public class IdBasedSource<T> extends GraphStage<SourceShape<T>> {

    private final StreamId<T> streamId;
    private final Outlet<T> out;

    public IdBasedSource(StreamId<T> streamId) {
        this.streamId = streamId;
        this.out = Outlet.create("IdBasedSource_" + streamId.toString() + ".out");
    }

    @Override
    public SourceShape<T> shape() {
        return new SourceShape<>(out);
    }

    @Override
    public GraphStageLogic createLogic(Attributes inheritedAttributes) {

        SourceSubscriber subscriber = getSourceSubscriber(inheritedAttributes);

        return new GraphStageLogic(shape()) {
            {
                setHandler(out, new AbstractOutHandler() {
                    @Override
                    public void onPull() throws Exception {
                        push(out, subscriber.latestValue);
                        if (subscriber.live) {
                            subscriber.subscription.request(1); // TODO tune request amount smartly
                        } else {
                            completeStage();
                        }
                    }

                    @Override
                    public void onDownstreamFinish() throws Exception {
                        super.onDownstreamFinish();
                        subscriber.subscription.cancel();
                    }
                });
            }

        };
    }

    private SourceSubscriber getSourceSubscriber(Attributes inheritedAttributes) {
        DiscoveryService service = getDiscoveryService(inheritedAttributes);
        Publisher<T> publisher = service.discover(streamId);
        SourceSubscriber subscriber = new SourceSubscriber();
        publisher.subscribe(subscriber);
        return subscriber;
    }

    // from attributes? injected?
    // currently package protected for testing purposes
    protected DiscoveryService getDiscoveryService(Attributes inheritedAttributes) {
        Optional<StreamDiscovery> discovery = inheritedAttributes.getAttribute(StreamDiscovery.class);
        return discovery.get().service();
    }

    private class SourceSubscriber implements Subscriber<T> {

        private Subscription subscription;
        private T latestValue;
        private boolean live = false;

        @Override
        public void onSubscribe(Subscription newSubscription) {
            this.subscription = newSubscription;
            newSubscription.request(1);
            live = true;
        }

        @Override
        public void onNext(T value) {
            latestValue = value;
        }

        @Override
        public void onError(Throwable error) {
            // TODO check reactive streams behavior, probably just log
            live = false;
        }

        @Override
        public void onComplete() {
            // TODO check reactive streams behavior, probably just log
            live = false;
        }
    }

}