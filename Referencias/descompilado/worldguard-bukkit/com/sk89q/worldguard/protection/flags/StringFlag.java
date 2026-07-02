/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import javax.annotation.Nullable;

public class StringFlag
extends Flag<String> {
    private final String defaultValue;

    public StringFlag(String name) {
        super(name);
        this.defaultValue = null;
    }

    public StringFlag(String name, String defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    public StringFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
        this.defaultValue = null;
    }

    public StringFlag(String name, RegionGroup defaultGroup, String defaultValue) {
        super(name, defaultGroup);
        this.defaultValue = defaultValue;
    }

    @Override
    @Nullable
    public String getDefault() {
        return this.defaultValue;
    }

    @Override
    public String parseInput(FlagContext context) throws InvalidFlagFormat {
        String lines = context.getUserInput().replaceAll("(?<!\\\\)\\\\n", "\n").replaceAll("\\\\\\\\n", "\\\\n");
        lines = CommandUtils.replaceColorMacros(lines);
        return lines;
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

