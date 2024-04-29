package com.cc.flox.utils;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author cc
 * @date 2024/4/29
 */
public class StreamUtils {

    /**
     * @param consumer consumer
     * @param <T>      T
     * @return t
     */
    public static <T> Consumer<T> withCounter(BiConsumer<Integer, T> consumer) {
        AtomicInteger counter = new AtomicInteger(0);
        return item -> consumer.accept(counter.getAndIncrement(), item);
    }
}

