package io.quarkus.arc.test.interceptors;

import jakarta.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class Counter {

    private AtomicInteger counter = new AtomicInteger();

    int incrementAndGet() {
        return counter.incrementAndGet();
    }

    void reset() {
        counter.set(0);
    }

    int get() {
        return counter.get();
    }

}
