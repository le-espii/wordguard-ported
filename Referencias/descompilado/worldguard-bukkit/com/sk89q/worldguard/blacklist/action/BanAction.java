/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.blacklist.action;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.blacklist.BlacklistEntry;
import com.sk89q.worldguard.blacklist.action.Action;
import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public class BanAction
implements Action {
    private final BlacklistEntry entry;

    public BanAction(BlacklistEntry entry) {
        Preconditions.checkNotNull((Object)entry);
        this.entry = entry;
    }

    @Override
    public ActionResult apply(BlacklistEvent event, boolean silent, boolean repeating, boolean forceRepeat) {
        if (silent) {
            return ActionResult.INHERIT;
        }
        if (event.getPlayer() != null) {
            String message = this.entry.getMessage();
            if (message != null) {
                event.getPlayer().ban("Banned: " + String.format(message, event.getTarget().getFriendlyName()));
            } else {
                event.getPlayer().ban("Banned: You can't " + event.getDescription() + " " + event.getTarget().getFriendlyName());
            }
        }
        return ActionResult.INHERIT;
    }
}

