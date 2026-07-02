/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;

public class CommandStringFlag
extends Flag<String> {
    public CommandStringFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public CommandStringFlag(String name) {
        super(name);
    }

    @Override
    public String parseInput(FlagContext context) throws InvalidFlagFormat {
        Object input = context.getUserInput();
        if (!((String)(input = ((String)input).trim())).startsWith("/")) {
            input = "/" + (String)input;
        }
        return ((String)input).toLowerCase();
    }

    @Override
    public String unmarshal(Object o) {
        if (o instanceof String) {
            return (String)o;
        }
        return null;
    }

    @Override
    public Object marshal(String o) {
        return o;
    }
}

