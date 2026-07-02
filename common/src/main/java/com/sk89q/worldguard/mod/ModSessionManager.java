package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.session.AbstractSessionManager;

public class ModSessionManager extends AbstractSessionManager implements Runnable {
    
    public ModSessionManager(WorldGuardPlatform platform) {
        super();
    }

    @Override
    public void resetAllStates() {
        resetState(null);
    }

    @Override
    public void run() {
        // Ticks de las sesiones
    }

    @Override
    public boolean hasBypass(LocalPlayer player, World world) {
        return player.hasPermission("worldguard.bypass");
    }
}
