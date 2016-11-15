/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import java.util.Objects;
import java.util.function.Function;

public class ChainsTries {

    public static void main(String... args) {

        ChainBuilder<Object, String> builder = start(Objects::toString);

    }

    public static <T, R> ChainBuilder<T, R> start() {
        return new ChainBuilder<>();
    }

    public static <T, R> ChainBuilder<T, R> start(Function<T, R> sfunc) {
        return new ChainBuilder<>();
    }

    
    
    
    
    public static class ChainBuilder<T, R> {

        
    }

    public static class Chain<T, R> {
    }

}
