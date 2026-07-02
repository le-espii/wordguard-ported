/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.Command
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.NestedCommand
 *  com.sk89q.worldedit.extension.platform.Actor
 */
package com.sk89q.worldguard.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.WorldGuardCommands;
import com.sk89q.worldguard.commands.region.MemberCommands;
import com.sk89q.worldguard.commands.region.RegionCommands;

public class ProtectionCommands {
    private final WorldGuard worldGuard;

    public ProtectionCommands(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    @Command(aliases={"region", "regions", "rg"}, desc="Region management commands")
    @NestedCommand(value={RegionCommands.class, MemberCommands.class})
    public void region(CommandContext args, Actor sender) {
    }

    @Command(aliases={"worldguard", "wg"}, desc="WorldGuard commands")
    @NestedCommand(value={WorldGuardCommands.class})
    public void worldGuard(CommandContext args, Actor sender) {
    }
}

