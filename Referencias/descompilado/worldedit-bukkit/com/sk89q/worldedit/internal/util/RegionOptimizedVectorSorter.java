/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 */
package com.sk89q.worldedit.internal.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sk89q.worldedit.math.BitMath;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegionOptimizedVectorSorter {
    private static final int CHUNK_Z_SHIFT = 20;
    private static final int CHUNK_X_SHIFT = 25;
    private static final int REGION_Z_SHIFT = 30;
    private static final int REGION_X_SHIFT = 47;
    private static final long REGION_X_MASK = (long)BitMath.mask(17) << 47;
    private static final long REGION_Z_MASK = (long)BitMath.mask(17) << 30;
    private static final long CHUNK_X_MASK = (long)BitMath.mask(5) << 25;
    private static final long CHUNK_Z_MASK = (long)BitMath.mask(5) << 20;
    private static final int Y_MAX = BitMath.mask(20);
    private static final long FLIP_REGION_X_SIGN = Long.MIN_VALUE;
    private static final long FLIP_REGION_Z_SIGN = 0x400000000000L;
    private static final int NUMBER_OF_BITS = 64;
    private static final int BITS_PER_SORT = 16;
    private static final int MAX_FOR_BPS = 65536;
    private static final int MASK_FOR_BPS = 65535;
    private static final int NUMBER_OF_SORTS = 4;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    static int PARALLELISM_THRESHOLD = NUMBER_OF_CORES == 1 ? Integer.MAX_VALUE : 200000;
    private static final ExecutorService SORT_SVC = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("worldedit-sort-svc-%d").build());

    private static long key(BlockVector3 elem) {
        long x = elem.x();
        long z = elem.z();
        return x << 38 & REGION_X_MASK ^ Long.MIN_VALUE | z << 21 & REGION_Z_MASK ^ 0x400000000000L | x << 21 & CHUNK_X_MASK | z << 16 & CHUNK_Z_MASK | (long)(Y_MAX - elem.y());
    }

    public static void sort(List<BlockVector3> vectors) {
        RegionOptimizedVectorSorter.sort(vectors.size() >= PARALLELISM_THRESHOLD, vectors);
    }

    public static void sort(boolean parallel, List<BlockVector3> vectors) {
        int size = vectors.size();
        if (size == 0 || size == 1) {
            return;
        }
        BlockVector3[] source = vectors.toArray(new BlockVector3[0]);
        BlockVector3[] sorted = new BlockVector3[size];
        source = !parallel ? RegionOptimizedVectorSorter.serialSort(source, size, sorted) : RegionOptimizedVectorSorter.parallelSort(source, size, sorted);
        ListIterator<BlockVector3> it = vectors.listIterator();
        for (BlockVector3 blockVector3 : source) {
            it.next();
            it.set(blockVector3);
        }
    }

    private static BlockVector3[] parallelSort(BlockVector3[] source, int size, BlockVector3[] sorted) {
        int[][] counts = new int[NUMBER_OF_CORES][65536];
        int[] finalCounts = new int[65536];
        int[] keys = new int[size];
        ArrayList<Future<int[]>> tasks = new ArrayList<Future<int[]>>(NUMBER_OF_CORES);
        int kStep = (size + NUMBER_OF_CORES - 1) / NUMBER_OF_CORES;
        for (int p = 0; p < 4; ++p) {
            BlockVector3[] currentSource = source;
            int shift = 16 * p;
            for (int c = 0; c < NUMBER_OF_CORES; ++c) {
                int[] nArray = counts[c];
                int kStart = kStep * c;
                int kEnd = Math.min(kStart + kStep, size);
                tasks.add(SORT_SVC.submit(() -> {
                    for (int i = kStart; i < kEnd; ++i) {
                        int k;
                        keys[i] = k = (int)(RegionOptimizedVectorSorter.key(currentSource[i]) >>> shift) & 0xFFFF;
                        int n = k;
                        localCounts[n] = localCounts[n] + 1;
                    }
                    return localCounts;
                }));
            }
            for (Future future : tasks) {
                try {
                    future.get();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            for (int c = 0; c < NUMBER_OF_CORES; ++c) {
                int[] nArray = counts[c];
                for (int i = 0; i < 65536; ++i) {
                    int n = i;
                    finalCounts[n] = finalCounts[n] + nArray[i];
                    nArray[i] = 0;
                }
            }
            tasks.clear();
            RegionOptimizedVectorSorter.copyByCounts(size, source, sorted, keys, finalCounts);
            BlockVector3[] temp = source;
            source = sorted;
            sorted = temp;
        }
        return source;
    }

    private static BlockVector3[] serialSort(BlockVector3[] source, int size, BlockVector3[] sorted) {
        int[] counts = new int[65536];
        int[] keys = new int[size];
        for (int p = 0; p < 4; ++p) {
            for (int i = 0; i < size; ++i) {
                int k;
                keys[i] = k = (int)(RegionOptimizedVectorSorter.key(source[i]) >>> 16 * p) & 0xFFFF;
                int n = k;
                counts[n] = counts[n] + 1;
            }
            RegionOptimizedVectorSorter.copyByCounts(size, source, sorted, keys, counts);
            BlockVector3[] temp = source;
            source = sorted;
            sorted = temp;
        }
        return source;
    }

    private static void copyByCounts(int size, BlockVector3[] source, BlockVector3[] sorted, int[] keys, int[] finalCounts) {
        int lastCount = finalCounts[0];
        int i = 1;
        while (i < 65536) {
            int n = i++;
            int n2 = finalCounts[n] + lastCount;
            finalCounts[n] = n2;
            lastCount = n2;
        }
        for (i = size - 1; i >= 0; --i) {
            int key;
            int n = key = keys[i];
            int n3 = finalCounts[n] - 1;
            finalCounts[n] = n3;
            int count = n3;
            sorted[count] = source[i];
        }
        Arrays.fill(finalCounts, 0);
    }

    private RegionOptimizedVectorSorter() {
    }
}

