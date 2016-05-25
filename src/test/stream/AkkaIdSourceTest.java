/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import static org.junit.Assert.fail;

import org.junit.Test;

import akka.NotUsed;
import akka.stream.Attributes;
import akka.stream.Outlet;
import akka.stream.SourceShape;
import akka.stream.javadsl.Source;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

public class AkkaIdSourceTest {

    @Test
    public void createIdSource() {
        Source<Object, NotUsed> source = Source.fromPublisher(null);
        fail("Not yet implemented");
    }

    public class IdBasedSource<T> extends GraphStage<SourceShape<T>> {

        private final Outlet<T> out = Outlet.create("NumberSource.out");
        private final SourceShape<T> shape = SourceShape.of(out);

        private final StreamId<T> streamId;

        public IdBasedSource(StreamId<T> streamId) {
            this.streamId = streamId;
        }

        @Override
        public SourceShape<T> shape() {
            return this.shape;
        }

        @Override
        public GraphStageLogic createLogic(Attributes inheritedAttributes) {
            DiscoveryService service = toBeImplemented();
            ReactStream<T> stream = service.discover(streamId);
            
            
           final  Source<T, NotUsed> publisherSource = Source.fromPublisher(ReactStreams.publisherFrom(stream));

            return new GraphStageLogic(shape()) {
                {
                    setHandler(out, new AbstractOutHandler() {
                        @Override
                        public void onPull() throws Exception {
                            // push(out, publisherSource.);
                        }
                    });
                }

            };
        }

        /**
         * @return
         */
        private DiscoveryService toBeImplemented() {
            return new DiscoveryService() {

                @Override
                public <T> ReactStream<T> discover(StreamId<T> id) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
        } // to be gotten from attributes (hopefully ;-)
    }

}
