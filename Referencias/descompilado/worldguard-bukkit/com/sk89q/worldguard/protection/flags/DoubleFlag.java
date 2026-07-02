/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.NumberFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;

public class DoubleFlag
extends NumberFlag<Double> {
    public DoubleFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public DoubleFlag(String name) {
        super(name);
    }

    @Override
    public Double parseInput(FlagContext context) throws InvalidFlagFormat {
        return context.getUserInputAsDouble();
    }

    @Override
    public Double unmarshal(Object o) {
        if (o instanceof Double) {
            return (Double)o;
        }
        if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
        return null;
    }

    @Override
    public Object marshal(Double o) {
        return o;
    }
}

