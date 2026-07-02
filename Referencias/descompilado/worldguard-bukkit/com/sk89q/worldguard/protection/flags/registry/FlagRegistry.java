/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags.registry;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public interface FlagRegistry
extends Iterable<Flag<?>> {
    public void register(Flag<?> var1) throws FlagConflictException;

    public void registerAll(Collection<Flag<?>> var1);

    @Nullable
    public Flag<?> get(String var1);

    public List<Flag<?>> getAll();

    public Map<Flag<?>, Object> unmarshal(Map<String, Object> var1, boolean var2);

    public int size();
}

