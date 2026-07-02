/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.SettableFuture
 */
package com.sk89q.worldguard.util.profiler;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.sk89q.worldguard.util.profiler.StackNode;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class SamplerBuilder {
    private static final Timer timer = new Timer("WorldGuard Sampler", true);
    private int interval = 100;
    private long runTime = TimeUnit.MINUTES.toMillis(5L);
    private Predicate<ThreadInfo> threadFilter = thread -> true;

    public int getInterval() {
        return this.interval;
    }

    public void setInterval(int interval) {
        Preconditions.checkArgument((interval >= 1 ? 1 : 0) != 0, (Object)"interval >= 1");
        this.interval = interval;
    }

    public Predicate<ThreadInfo> getThreadFilter() {
        return this.threadFilter;
    }

    public void setThreadFilter(Predicate<ThreadInfo> threadFilter) {
        Preconditions.checkNotNull(threadFilter, (Object)"threadFilter");
        this.threadFilter = threadFilter;
    }

    public long getRunTime(TimeUnit timeUnit) {
        return timeUnit.convert(this.runTime, TimeUnit.MILLISECONDS);
    }

    public void setRunTime(long time, TimeUnit timeUnit) {
        Preconditions.checkArgument((time > 0L ? 1 : 0) != 0, (Object)"time > 0");
        this.runTime = timeUnit.toMillis(time);
    }

    public Sampler start() {
        Sampler sampler = new Sampler(this.interval, this.threadFilter, System.currentTimeMillis() + this.runTime);
        timer.scheduleAtFixedRate((TimerTask)sampler, 0L, (long)this.interval);
        return sampler;
    }

    public static class Sampler
    extends TimerTask {
        private final int interval;
        private final Predicate<ThreadInfo> threadFilter;
        private final long endTime;
        private final SortedMap<String, StackNode> nodes = new TreeMap<String, StackNode>();
        private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        private final SettableFuture<Sampler> future = SettableFuture.create();

        private Sampler(int interval, Predicate<ThreadInfo> threadFilter, long endTime) {
            this.interval = interval;
            this.threadFilter = threadFilter;
            this.endTime = endTime;
        }

        public ListenableFuture<Sampler> getFuture() {
            return this.future;
        }

        private Map<String, StackNode> getData() {
            return this.nodes;
        }

        private StackNode getNode(String name) {
            StackNode node = (StackNode)this.nodes.get(name);
            if (node == null) {
                node = new StackNode(name);
                this.nodes.put(name, node);
            }
            return node;
        }

        @Override
        public boolean cancel() {
            this.future.setException((Throwable)new CancellationException());
            return super.cancel();
        }

        @Override
        public synchronized void run() {
            try {
                ThreadInfo[] threadDumps;
                if (this.endTime <= System.currentTimeMillis()) {
                    this.future.set((Object)this);
                    this.cancel();
                    return;
                }
                for (ThreadInfo threadInfo : threadDumps = this.threadBean.dumpAllThreads(false, false)) {
                    String threadName = threadInfo.getThreadName();
                    StackTraceElement[] stack = threadInfo.getStackTrace();
                    if (threadName == null || stack == null || !this.threadFilter.test(threadInfo)) continue;
                    StackNode node = this.getNode(threadName);
                    node.log(stack, this.interval);
                }
            }
            catch (Throwable t) {
                this.future.setException(t);
                super.cancel();
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, StackNode> entry : this.getData().entrySet()) {
                builder.append(entry.getKey());
                builder.append(" ");
                builder.append(entry.getValue().getTotalTime()).append("ms");
                builder.append("\n");
                entry.getValue().writeString(builder, 1);
            }
            return builder.toString();
        }
    }
}

