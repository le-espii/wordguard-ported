/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.gamemode;

import com.sk89q.worldedit.world.gamemode.GameMode;
import javax.annotation.Nullable;

public final class GameModes {
    public static final GameMode SURVIVAL = GameModes.register("survival");
    public static final GameMode CREATIVE = GameModes.register("creative");
    public static final GameMode ADVENTURE = GameModes.register("adventure");
    public static final GameMode SPECTATOR = GameModes.register("spectator");

    private GameModes() {
    }

    private static GameMode register(String id) {
        return GameModes.register(new GameMode(id));
    }

    public static GameMode register(GameMode gameMode) {
        return GameMode.REGISTRY.register(gameMode.id(), gameMode);
    }

    @Nullable
    public static GameMode get(String id) {
        return GameMode.REGISTRY.get(id);
    }
}

