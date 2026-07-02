/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.collect.Sets;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SetFlag<T>
extends Flag<Set<T>> {
    private Flag<T> subFlag;

    public SetFlag(String name, RegionGroup defaultGroup, Flag<T> subFlag) {
        super(name, defaultGroup);
        Objects.requireNonNull(subFlag, "SubFlag cannot be null.");
        this.subFlag = subFlag;
    }

    public SetFlag(String name, Flag<T> subFlag) {
        super(name);
        Objects.requireNonNull(subFlag, "SubFlag cannot be null.");
        this.subFlag = subFlag;
    }

    public Flag<T> getType() {
        return this.subFlag;
    }

    @Override
    public Set<T> parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        if (input.isEmpty()) {
            return Sets.newHashSet();
        }
        HashSet items = Sets.newHashSet();
        for (String str : input.split(",")) {
            FlagContext copy = context.copyWith(null, str, null);
            items.add(this.subFlag.parseInput(copy));
        }
        return items;
    }

    @Override
    public Set<T> unmarshal(Object o) {
        if (o instanceof Collection) {
            Collection collection = (Collection)o;
            HashSet<T> items = new HashSet<T>();
            for (Object sub : collection) {
                T item = this.subFlag.unmarshal(sub);
                if (item == null) continue;
                items.add(item);
            }
            return items;
        }
        return null;
    }

    @Override
    public Object marshal(Set<T> o) {
        ArrayList<Object> list = new ArrayList<Object>();
        for (T item : o) {
            list.add(this.subFlag.marshal(item));
        }
        return list;
    }
}

