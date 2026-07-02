/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags.registry;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import javax.annotation.Nullable;

public class UnknownFlag
extends Flag<Object> {
    public UnknownFlag(String name) {
        super(name);
    }

    @Override
    public Object parseInput(FlagContext context) throws InvalidFlagFormat {
        throw new InvalidFlagFormat("The plugin that registered this flag is not currently installed");
    }

    @Override
    public Object unmarshal(@Nullable Object o) {
        return o;
    }

    @Override
    public Object marshal(Object o) {
        return o;
    }
}

