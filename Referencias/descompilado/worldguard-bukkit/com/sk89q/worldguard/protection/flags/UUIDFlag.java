/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import java.util.UUID;
import javax.annotation.Nullable;

public class UUIDFlag
extends Flag<UUID> {
    public UUIDFlag(String name, @Nullable RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public UUIDFlag(String name) {
        super(name);
    }

    @Override
    public UUID parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        if ("self".equalsIgnoreCase(input)) {
            return context.getSender().getUniqueId();
        }
        try {
            return UUID.fromString(input);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidFlagFormat("Not a valid uuid: " + input);
        }
    }

    @Override
    public UUID unmarshal(@Nullable Object o) {
        if (!(o instanceof String)) {
            return null;
        }
        try {
            return UUID.fromString((String)o);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Object marshal(UUID o) {
        return o.toString();
    }
}

