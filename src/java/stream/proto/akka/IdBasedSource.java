package stream.proto.akka;

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
import stream.DiscoveryService;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;

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
        ReactStream<T> stream = service.discover(streamId);
        Publisher<T> publisher = ReactStreams.publisherFrom(stream);
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
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
            live = true;
        }

        @Override
        public void onNext(T t) {
            latestValue = t;
        }

        @Override
        public void onError(Throwable t) {
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