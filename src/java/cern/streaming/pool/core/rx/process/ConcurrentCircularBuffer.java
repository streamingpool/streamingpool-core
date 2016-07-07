package cern.streaming.pool.core.rx.process;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentCircularBuffer<T> {

    private final AtomicLong nextIndex = new AtomicLong(0);
    private final AtomicLong firstIndex = new AtomicLong(0);
    private final AtomicInteger length = new AtomicInteger(1);
    
    private final ConcurrentHashMap<Long, T> elements = new ConcurrentHashMap<>();

    public void add(T value) {
        long index = nextIndex.getAndIncrement();
        elements.put(index, value);
        cleanup();
    }

    public void clear() {
        cleanUpTo(nextIndex.get());
    }

    private void cleanup() {
        long newFirstIndex = nextIndex.get() - length.get();
        cleanUpTo(newFirstIndex);
    }

    private void cleanUpTo(long newFirstIndex) {
        long oldFirstIndex = firstIndex.getAndSet(newFirstIndex);
        for (long i = oldFirstIndex; i < newFirstIndex; i++) {
            elements.remove(i);
        }
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (long i = firstIndex.get(), next = nextIndex.get(); i < next; i++) {
            T element = elements.get(i);
            /* we have to check for null here, because a concurrent modification could have removed it in the meantime */
            if (element != null) {
                list.add(element);
            }
        }
        return list;
    }

    public void setLength(int newLength) {
        checkArgument(newLength >= 0, "buffer length must be >= 0 but was set to " + newLength);
        length.set(newLength);
    }
}
