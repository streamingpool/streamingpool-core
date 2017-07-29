import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.impl.LocalPool;
import org.streamingpool.core.service.streamid.CombineWithLatestStreamId;
import org.streamingpool.core.service.streamid.DelayedStreamId;
import org.streamingpool.core.service.streamid.DerivedStreamId;
import org.streamingpool.core.service.streamid.FilteredStreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

import java.io.IOException;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Andrea on 29/07/2017.
 */
public class TryDebug extends AbstractStreamTest implements RxStreamSupport{

    @Test
    public void test() throws IOException {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        String style = Stream.of(
                "shape: box;",
                "size: 30px, 20px;",
                "fill-mode: plain;",
                "text-alignment: right;",
                "text-padding: 5px;",
                "text-offset: 40px, 0px;",
                "fill-color: red;",
                "stroke-mode: plain;",
                "stroke-color: blue;").collect(Collectors.joining());

        LocalPool.graph.addAttribute("ui.quality");
        LocalPool.graph.addAttribute("ui.antialias");

        LocalPool.graph.setAttribute("ui.stylesheet", "node {" + style + "}");

        StreamId<String> source = provide(Flowable.just("A")).withUniqueStreamId();
        DerivedStreamId<String, String> derive = DerivedStreamId.derive(source, v -> "Derived " + v);
        FilteredStreamId<String> filter = FilteredStreamId.filterBy(derive, any -> true);
        DelayedStreamId<String> trigger = DelayedStreamId.delayBy(filter, Duration.ofSeconds(1));
        StreamId<String> data = provide(Flowable.just("DATA")).withUniqueStreamId();
        CombineWithLatestStreamId<String, String, String> withLatestFrom = CombineWithLatestStreamId.dataPropagated(trigger, data);



        TestSubscriber<String> subscriber =
                rxFrom(withLatestFrom).test();

        subscriber.awaitTerminalEvent();

        System.out.println(subscriber.values());

        LocalPool.graph.display();
        System.in.read();
    }


}
