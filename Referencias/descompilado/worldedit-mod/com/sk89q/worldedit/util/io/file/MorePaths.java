/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Streams
 */
package com.sk89q.worldedit.util.io.file;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.stream.Stream;

public class MorePaths {
    public static Comparator<Path> oldestFirst() {
        return Comparator.comparing(x -> {
            try {
                return Files.getLastModifiedTime(x, new LinkOption[0]);
            }
            catch (IOException e) {
                return FileTime.from(Instant.EPOCH);
            }
        });
    }

    public static Comparator<Path> newestFirst() {
        return MorePaths.oldestFirst().reversed();
    }

    public static Stream<Path> iterPaths(Path path) {
        ArrayDeque<Path> parents = new ArrayDeque<Path>(path.getNameCount());
        for (Path next = path; next != null; next = next.getParent()) {
            parents.addFirst(next);
        }
        return ImmutableList.copyOf(parents).stream();
    }

    public static Spliterator<Path> optimizedSpliterator(Path path) {
        return Arrays.spliterator((Path[])Streams.stream((Iterable)path).toArray(Path[]::new));
    }

    private MorePaths() {
    }
}

