/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.gamemode.GameMode
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import javax.annotation.Nullable;

public class GameModeFlag
extends FlagValueChangeHandler<GameMode> {
    public static final Factory FACTORY = new Factory();
    private GameMode originalGameMode;
    private GameMode setGameMode;

    public GameModeFlag(Session session) {
        super(session, Flags.GAME_MODE);
    }

    public GameMode getOriginalGameMode() {
        return this.originalGameMode;
    }

    public GameMode getSetGameMode() {
        return this.setGameMode;
    }

    private void updateGameMode(LocalPlayer player, @Nullable GameMode newValue, World world) {
        if (!this.getSession().getManager().hasBypass(player, world) && newValue != null) {
            if (player.getGameMode() != newValue) {
                this.originalGameMode = player.getGameMode();
                player.setGameMode(newValue);
            } else if (this.originalGameMode == null) {
                this.originalGameMode = WorldGuard.getInstance().getPlatform().getDefaultGameMode();
            }
        } else if (this.originalGameMode != null) {
            GameMode mode = this.originalGameMode;
            this.originalGameMode = null;
            player.setGameMode(mode);
        }
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, GameMode value) {
        this.updateGameMode(player, value, player.getWorld());
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, GameMode currentValue, GameMode lastValue, MoveType moveType) {
        this.updateGameMode(player, currentValue, (World)to.getExtent());
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, GameMode lastValue, MoveType moveType) {
        this.updateGameMode(player, null, (World)player.getExtent());
        return true;
    }

    public static class Factory
    extends Handler.Factory<GameModeFlag> {
        @Override
        public GameModeFlag create(Session session) {
            return new GameModeFlag(session);
        }
    }
}

