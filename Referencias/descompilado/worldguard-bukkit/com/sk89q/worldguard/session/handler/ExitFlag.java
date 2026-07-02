/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;

public class ExitFlag
extends FlagValueChangeHandler<StateFlag.State> {
    public static final Factory FACTORY = new Factory();
    private static final long MESSAGE_THRESHOLD = 2000L;
    private String storedMessage;
    private boolean exitViaTeleport = false;
    private long lastMessage;

    public ExitFlag(Session session) {
        super(session, Flags.EXIT);
    }

    private void update(LocalPlayer localPlayer, ApplicableRegionSet set, boolean allowed) {
        if (!allowed) {
            this.storedMessage = set.queryValue(localPlayer, Flags.EXIT_DENY_MESSAGE);
            this.exitViaTeleport = set.testState(localPlayer, Flags.EXIT_VIA_TELEPORT);
        }
    }

    private void sendMessage(LocalPlayer player) {
        long now = System.currentTimeMillis();
        if (now - this.lastMessage > 2000L && this.storedMessage != null && !this.storedMessage.isEmpty()) {
            player.printRaw(CommandUtils.replaceColorMacros(this.storedMessage));
            this.lastMessage = now;
        }
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, StateFlag.State value) {
        this.update(player, set, StateFlag.test(value));
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State currentValue, StateFlag.State lastValue, MoveType moveType) {
        Boolean override;
        if (this.getSession().getManager().hasBypass(player, (World)from.getExtent())) {
            return true;
        }
        boolean lastAllowed = StateFlag.test(lastValue);
        boolean allowed = StateFlag.test(currentValue);
        if (!(!allowed || lastAllowed || moveType.isTeleport() && this.exitViaTeleport || !moveType.isCancellable() || (override = toSet.queryValue(player, Flags.EXIT_OVERRIDE)) != null && override.booleanValue())) {
            this.sendMessage(player);
            return false;
        }
        this.update(player, toSet, allowed);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State lastValue, MoveType moveType) {
        Boolean override;
        if (this.getSession().getManager().hasBypass(player, (World)from.getExtent())) {
            return true;
        }
        boolean lastAllowed = StateFlag.test(lastValue);
        if (!(lastAllowed || !moveType.isCancellable() || (override = toSet.queryValue(player, Flags.EXIT_OVERRIDE)) != null && override.booleanValue())) {
            this.sendMessage(player);
            return false;
        }
        return true;
    }

    public static class Factory
    extends Handler.Factory<ExitFlag> {
        @Override
        public ExitFlag create(Session session) {
            return new ExitFlag(session);
        }
    }
}

