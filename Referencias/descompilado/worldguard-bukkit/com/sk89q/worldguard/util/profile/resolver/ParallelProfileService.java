/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class ParallelProfileService
implements ProfileService {
    private final ProfileService resolver;
    private final ExecutorService executorService;
    private int profilesPerJob = 100;

    public ParallelProfileService(ProfileService resolver, ExecutorService executorService) {
        Preconditions.checkNotNull((Object)resolver);
        Preconditions.checkNotNull((Object)executorService);
        this.resolver = resolver;
        this.executorService = executorService;
    }

    public ParallelProfileService(ProfileService resolver, int numThreads) {
        this(resolver, Executors.newFixedThreadPool(numThreads));
    }

    public int getProfilesPerJob() {
        return this.profilesPerJob;
    }

    public void setProfilesPerJob(int profilesPerJob) {
        Preconditions.checkArgument((profilesPerJob >= 1 ? 1 : 0) != 0, (Object)"profilesPerJob must be >= 1");
        this.profilesPerJob = profilesPerJob;
    }

    @Override
    public int getIdealRequestLimit() {
        return this.resolver.getIdealRequestLimit();
    }

    protected int getEffectiveProfilesPerJob() {
        return Math.min(this.profilesPerJob, this.resolver.getIdealRequestLimit());
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        return this.resolver.findByName(name);
    }

    @Override
    public ImmutableList<Profile> findAllByName(Iterable<String> names) throws IOException, InterruptedException {
        ExecutorCompletionService<List> completion = new ExecutorCompletionService<List>(this.executorService);
        int count = 0;
        for (List partition : Iterables.partition(names, (int)this.getEffectiveProfilesPerJob())) {
            ++count;
            completion.submit(() -> this.resolver.findAllByName(partition));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < count; ++i) {
            try {
                builder.addAll((Iterable)completion.take().get());
                continue;
            }
            catch (ExecutionException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException)e.getCause();
                }
                throw new RuntimeException("Error occurred during the operation", e);
            }
        }
        return builder.build();
    }

    @Override
    public void findAllByName(Iterable<String> names, Predicate<Profile> consumer) throws IOException, InterruptedException {
        ExecutorCompletionService<Object> completion = new ExecutorCompletionService<Object>(this.executorService);
        int count = 0;
        for (List partition : Iterables.partition(names, (int)this.getEffectiveProfilesPerJob())) {
            ++count;
            completion.submit(() -> {
                this.resolver.findAllByName(partition, consumer);
                return null;
            });
        }
        Throwable throwable = null;
        for (int i = 0; i < count; ++i) {
            try {
                completion.take().get();
                continue;
            }
            catch (ExecutionException e) {
                throwable = e.getCause();
            }
        }
        if (throwable != null) {
            if (throwable instanceof IOException) {
                throw (IOException)throwable;
            }
            throw new RuntimeException("Error occurred during the operation", throwable);
        }
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        return this.resolver.findByUuid(uuid);
    }

    @Override
    public ImmutableList<Profile> findAllByUuid(Iterable<UUID> uuids) throws IOException, InterruptedException {
        ExecutorCompletionService<List> completion = new ExecutorCompletionService<List>(this.executorService);
        int count = 0;
        for (List partition : Iterables.partition(uuids, (int)this.getEffectiveProfilesPerJob())) {
            ++count;
            completion.submit(() -> this.resolver.findAllByUuid(partition));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < count; ++i) {
            try {
                builder.addAll((Iterable)completion.take().get());
                continue;
            }
            catch (ExecutionException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException)e.getCause();
                }
                throw new RuntimeException("Error occurred during the operation", e);
            }
        }
        return builder.build();
    }

    @Override
    public void findAllByUuid(Iterable<UUID> uuids, Predicate<Profile> consumer) throws IOException, InterruptedException {
        ExecutorCompletionService<Object> completion = new ExecutorCompletionService<Object>(this.executorService);
        int count = 0;
        for (List partition : Iterables.partition(uuids, (int)this.getEffectiveProfilesPerJob())) {
            ++count;
            completion.submit(() -> {
                this.resolver.findAllByUuid(partition, consumer);
                return null;
            });
        }
        Throwable throwable = null;
        for (int i = 0; i < count; ++i) {
            try {
                completion.take().get();
                continue;
            }
            catch (ExecutionException e) {
                throwable = e.getCause();
            }
        }
        if (throwable != null) {
            if (throwable instanceof IOException) {
                throw (IOException)throwable;
            }
            throw new RuntimeException("Error occurred during the operation", throwable);
        }
    }
}

