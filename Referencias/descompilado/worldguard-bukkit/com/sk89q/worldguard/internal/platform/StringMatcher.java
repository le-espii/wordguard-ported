/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.internal.platform;

import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public interface StringMatcher {
    public World matchWorld(Actor var1, String var2) throws CommandException;

    public List<LocalPlayer> matchPlayerNames(String var1);

    default public Iterable<? extends LocalPlayer> checkPlayerMatch(List<? extends LocalPlayer> players) throws CommandException {
        if (players.isEmpty()) {
            throw new CommandException("No players matched query.");
        }
        return players;
    }

    public Iterable<? extends LocalPlayer> matchPlayers(Actor var1, String var2) throws CommandException;

    default public LocalPlayer matchSinglePlayer(Actor sender, String filter) throws CommandException {
        Iterator<? extends LocalPlayer> players = this.matchPlayers(sender, filter).iterator();
        LocalPlayer match = players.next();
        if (players.hasNext()) {
            throw new CommandException("More than one player found! Use @<name> for exact matching.");
        }
        return match;
    }

    public Actor matchPlayerOrConsole(Actor var1, String var2) throws CommandException;

    default public Iterable<LocalPlayer> matchPlayers(LocalPlayer player) {
        return Lists.newArrayList((Object[])new LocalPlayer[]{player});
    }

    @Nullable
    public World getWorldByName(String var1);

    public String replaceMacros(Actor var1, String var2);
}

