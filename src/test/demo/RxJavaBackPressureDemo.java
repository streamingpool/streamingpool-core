/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Demo of the back-pressure management in rxJava, to assert that a slow observer will not penalize faster observers.
 * Default rxJava Buffer size for backpressure : 9223372036854775807 (Long.MAX_VALUE). Not sure how to modify it ...
 * 
 * @author garnierj
 */
public class RxJavaBackPressureDemo {

    public static void main(String[] args) throws InterruptedException {
        Observable<Long> observable = Observable.interval(1, TimeUnit.MILLISECONDS)
                .doOnRequest(size -> System.out.println("Requestesd size : " + size))
                .onBackpressureDrop(x -> System.out.println("Dropped " + x));

        subscribe(observable, 0, "FAST");
        subscribe(observable, 3, "SLOW");

        TimeUnit.SECONDS.sleep(10);
    }

    private static void subscribe(Observable<Long> observable, int sleepingTimeInSeconds, String name) {
        observable.observeOn(Schedulers.newThread()).subscribe(i -> {
            System.out.println(name + " " + i);
            try {
                TimeUnit.SECONDS.sleep(sleepingTimeInSeconds);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted: " + e);
            }
        }, System.out::println);
    }

}
