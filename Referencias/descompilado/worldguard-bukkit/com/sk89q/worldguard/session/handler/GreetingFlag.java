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

public class GreetingFlag
extends Handler {
    public static final Factory FACTORY = new Factory();
    private Set<String> lastMessageStack = Collections.emptySet();
    private Set<String> lastTitleStack = Collections.emptySet();

    public GreetingFlag(Session session) {
        super(session);
    }

    private Set<String> getMessages(LocalPlayer player, ApplicableRegionSet set, Flag<String> flag) {
        return Sets.newLinkedHashSet(set.queryAllValues(player, flag));
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        this.lastMessageStack = this.sendAndCollect(player, toSet, Flags.GREET_MESSAGE, this.lastMessageStack, MessagingUtil::sendStringToChat);
        this.lastTitleStack = this.sendAndCollect(player, toSet, Flags.GREET_TITLE, this.lastTitleStack, MessagingUtil::sendStringToTitle);
        return true;
    }

    private Set<String> sendAndCollect(LocalPlayer player, ApplicableRegionSet toSet, Flag<String> flag, Set<String> stack, BiConsumer<LocalPlayer, String> msgFunc) {
        Set<String> messages = this.getMessages(player, toSet, flag);
        for (String message : messages) {
            if (stack.contains(message)) continue;
            msgFunc.accept(player, message);
            break;
        }
        if (!(stack = Sets.newHashSet(messages)).isEmpty()) {
            for (ProtectedRegion region : toSet) {
                String message = (String)region.getFlag(flag);
                if (message == null) continue;
                stack.add(message);
            }
        }
        return stack;
    }

    public static class Factory
    extends Handler.Factory<GreetingFlag> {
        @Override
        public GreetingFlag create(Session session) {
            return new GreetingFlag(session);
        }
    }
}

