/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.gamemode.GameMode
 *  com.sk89q.worldedit.world.gamemode.GameModes
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import javax.annotation.Nullable;

@Deprecated
public class GameModeTypeFlag
extends Flag<GameMode> {
    protected GameModeTypeFlag(String name, @Nullable RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    protected GameModeTypeFlag(String name) {
        super(name);
    }

    @Override
    public GameMode parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        GameMode gamemode = this.unmarshal(input = input.trim());
        if (gamemode == null) {
            throw new InvalidFlagFormat("Unknown game mode: " + input);
        }
        return gamemode;
    }

    @Override
    public GameMode unmarshal(@Nullable Object o) {
        return GameModes.get((String)String.valueOf(o).toLowerCase());
    }

    @Override
    public Object marshal(GameMode o) {
        return o.id();
    }
}

