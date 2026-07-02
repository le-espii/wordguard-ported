/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.util.profile.Profile;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public interface ProfileService {
    public int getIdealRequestLimit();

    @Nullable
    public Profile findByName(String var1) throws IOException, InterruptedException;

    public ImmutableList<Profile> findAllByName(Iterable<String> var1) throws IOException, InterruptedException;

    public void findAllByName(Iterable<String> var1, Predicate<Profile> var2) throws IOException, InterruptedException;

    @Nullable
    public Profile findByUuid(UUID var1) throws IOException, InterruptedException;

    public ImmutableList<Profile> findAllByUuid(Iterable<UUID> var1) throws IOException, InterruptedException;

    public void findAllByUuid(Iterable<UUID> var1, Predicate<Profile> var2) throws IOException, InterruptedException;
}

