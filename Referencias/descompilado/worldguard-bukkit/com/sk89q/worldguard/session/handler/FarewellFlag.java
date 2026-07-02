/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.sk89q.worldedit.util.Location
 */
package com.sk89q.worldguard.session.handler;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import com.sk89q.worldguard.util.MessagingUtil;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

public class FarewellFlag
extends Handler {
    public static final Factory FACTORY = new Factory();
    private Set<String> lastMessageStack = Collections.emptySet();
    private Set<String> lastTitleStack = Collections.emptySet();

    public FarewellFlag(Session session) {
        super(session);
    }

    private Set<String> getMessages(LocalPlayer player, ApplicableRegionSet set, Flag<String> flag) {
        return Sets.newLinkedHashSet(set.queryAllValues(player, flag));
    }

    @Override
    public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
        this.lastMessageStack = this.getMessages(player, set, Flags.FAREWELL_MESSAGE);
        this.lastTitleStack = this.getMessages(player, set, Flags.FAREWELL_TITLE);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        this.lastMessageStack = this.collectAndSend(player, toSet, Flags.FAREWELL_MESSAGE, this.lastMessageStack, MessagingUtil::sendStringToChat);
        this.lastTitleStack = this.collectAndSend(player, toSet, Flags.FAREWELL_TITLE, this.lastTitleStack, MessagingUtil::sendStringToTitle);
        return true;
    }

    private Set<String> collectAndSend(LocalPlayer player, ApplicableRegionSet toSet, Flag<String> flag, Set<String> stack, BiConsumer<LocalPlayer, String> msgFunc) {
        Set<String> messages = this.getMessages(player, toSet, flag);
        if (!messages.isEmpty()) {
            for (ProtectedRegion region : toSet) {
                String message = (String)region.getFlag(flag);
                if (message == null) continue;
                messages.add(message);
            }
        }
        for (String message : stack) {
            if (messages.contains(message)) continue;
            msgFunc.accept(player, message);
            break;
        }
        return messages;
    }

    public static class Factory
    extends Handler.Factory<FarewellFlag> {
        @Override
        public FarewellFlag create(Session session) {
            return new FarewellFlag(session);
        }
    }
}

