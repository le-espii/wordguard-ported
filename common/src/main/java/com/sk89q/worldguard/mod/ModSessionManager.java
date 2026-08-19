package com.sk89q.worldguard.mod;

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
        // Las sesiones se tickean por jugador en ServerPlayerMixin#onTick,
        // no aquí; este Runnable no está programado en ningún scheduler.
    }

    // hasBypass() no se sobreescribe: AbstractSessionManager.hasBypass() ya
    // implementa la semántica correcta de WorldGuard (permiso por mundo
    // "worldguard.region.bypass.<mundo>" + respeto del toggle de sesión
    // "hasBypassDisabled"). Sobreescribirlo con un permiso plano rompía
    // el bypass por mundo y el toggle de sesión (ver auditoría, hallazgo C1).
}
