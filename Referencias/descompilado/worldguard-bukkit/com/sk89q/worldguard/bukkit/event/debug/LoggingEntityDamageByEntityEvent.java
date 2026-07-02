/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 */
package com.sk89q.worldguard.bukkit.event.debug;

import com.sk89q.worldguard.bukkit.event.debug.CancelAttempt;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogger;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogging;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class LoggingEntityDamageByEntityEvent
extends EntityDamageByEntityEvent
implements CancelLogging {
    private final CancelLogger logger = new CancelLogger();

    public LoggingEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, double damage) {
        super(damager, damagee, cause, damage);
    }

    @Override
    public List<CancelAttempt> getCancels() {
        return this.logger.getCancels();
    }

    public void setCancelled(boolean cancel) {
        this.logger.log(this.isCancelled(), cancel, new Exception().getStackTrace());
        super.setCancelled(cancel);
    }
}

