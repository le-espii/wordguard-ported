/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterators
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import java.util.Collection;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class Flag<T> {
    private static final Pattern VALID_NAME = Pattern.compile("^[:A-Za-z0-9\\-]{1,40}$");
    private final String name;
    private final RegionGroupFlag regionGroup;

    protected Flag(String name, @Nullable RegionGroup defaultGroup) {
        if (name != null && !Flag.isValidName(name)) {
            throw new IllegalArgumentException("Invalid flag name used");
        }
        this.name = name;
        this.regionGroup = defaultGroup != null ? new RegionGroupFlag(name + "-group", defaultGroup) : null;
    }

    protected Flag(String name) {
        this(name, RegionGroup.ALL);
    }

    public final String getName() {
        return this.name;
    }

    @Nullable
    public T getDefault() {
        return null;
    }

    @Nullable
    public T chooseValue(Collection<T> values) {
        return (T)Iterators.getNext(values.iterator(), null);
    }

    public boolean hasConflictStrategy() {
        return false;
    }

    public boolean implicitlySetWithMembership() {
        return false;
    }

    public boolean usesMembershipAsDefault() {
        return false;
    }

    public boolean requiresSubject() {
        return false;
    }

    public final RegionGroupFlag getRegionGroupFlag() {
        return this.regionGroup;
    }

    public abstract T parseInput(FlagContext var1) throws InvalidFlagFormat;

    public abstract T unmarshal(@Nullable Object var1);

    public abstract Object marshal(T var1);

    public String toString() {
        return this.getClass().getSimpleName() + "{name='" + this.name + "'}";
    }

    public static boolean isValidName(String name) {
        Preconditions.checkNotNull((Object)name, (Object)"name");
        return VALID_NAME.matcher(name).matches();
    }
}

