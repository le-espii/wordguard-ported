/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.NumberFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;

public class IntegerFlag
extends NumberFlag<Integer> {
    public IntegerFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public IntegerFlag(String name) {
        super(name);
    }

    @Override
    public Integer parseInput(FlagContext context) throws InvalidFlagFormat {
        return context.getUserInputAsInt();
    }

    @Override
    public Integer unmarshal(Object o) {
        if (o instanceof Integer) {
            return (Integer)o;
        }
        if (o instanceof Number) {
            return ((Number)o).intValue();
        }
        return null;
    }

    @Override
    public Object marshal(Integer o) {
        return o;
    }
}

