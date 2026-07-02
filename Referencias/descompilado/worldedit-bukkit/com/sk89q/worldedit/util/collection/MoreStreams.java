/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 */
package com.sk89q.worldedit.util.collection;

import com.google.common.collect.AbstractIterator;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MoreStreams {
    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> predicate) {
        return MoreStreams.takeUntil(stream, predicate.negate());
    }

    public static <T> Stream<T> takeUntil(Stream<T> stream, final Predicate<T> predicate) {
        final Spliterator spliterator = stream.spliterator();
        AbstractIterator iter = new AbstractIterator<T>(){
            private Iterator<T> source;
            {
                this.source = Spliterators.iterator(spliterator);
            }

            protected T computeNext() {
                Iterator src = Objects.requireNonNull(this.source);
                if (!src.hasNext()) {
                    return this.done();
                }
                Object next = src.next();
                if (predicate.test(next)) {
                    return this.done();
                }
                return next;
            }

            private T done() {
                this.source = null;
                return this.endOfData();
            }
        };
        int chars = spliterator.characteristics();
        return (Stream)StreamSupport.stream(Spliterators.spliterator(iter, spliterator.estimateSize(), chars &= 0xFFFFBFBF), stream.isParallel()).onClose(stream::close);
    }

    public static IntStream bufferStream(ByteBuffer buffer) {
        return IntStream.range(buffer.position(), buffer.limit()).map(buffer::get);
    }

    public static IntStream bufferStream(IntBuffer buffer) {
        return IntStream.range(buffer.position(), buffer.limit()).map(buffer::get);
    }

    public static LongStream bufferStream(LongBuffer buffer) {
        return IntStream.range(buffer.position(), buffer.limit()).mapToLong(buffer::get);
    }

    private MoreStreams() {
    }
}

