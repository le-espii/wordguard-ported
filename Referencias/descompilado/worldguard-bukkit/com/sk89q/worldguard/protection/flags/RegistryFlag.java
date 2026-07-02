/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.registry.Keyed
 *  com.sk89q.worldedit.registry.Registry
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.Registry;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class RegistryFlag<T extends Keyed>
extends Flag<T> {
    private final Registry<T> registry;

    public RegistryFlag(String name, Registry<T> registry) {
        super(name);
        Objects.requireNonNull(registry, "registry cannot be null.");
        this.registry = registry;
    }

    public RegistryFlag(String name, @Nullable RegionGroup defaultGroup, Registry<T> registry) {
        super(name, defaultGroup);
        Objects.requireNonNull(registry, "registry cannot be null.");
        this.registry = registry;
    }

    @Override
    public T parseInput(FlagContext context) throws InvalidFlagFormat {
        String key = context.getUserInput().trim().toLowerCase(Locale.ROOT);
        return (T)Optional.ofNullable(this.registry.get(key)).orElseThrow(() -> new InvalidFlagFormat("Unknown " + this.registry.getName() + ": " + key));
    }

    public Registry<T> getRegistry() {
        return this.registry;
    }

    @Override
    public T unmarshal(@Nullable Object o) {
        return (T)this.registry.get(String.valueOf(o).toLowerCase(Locale.ROOT));
    }

    @Override
    public Object marshal(T o) {
        return o.id();
    }
}

