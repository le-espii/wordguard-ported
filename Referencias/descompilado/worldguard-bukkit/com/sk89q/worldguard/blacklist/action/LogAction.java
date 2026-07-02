/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.blacklist.action;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.blacklist.Blacklist;
import com.sk89q.worldguard.blacklist.BlacklistEntry;
import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.action.RepeatGuardedAction;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public class LogAction
extends RepeatGuardedAction {
    private final Blacklist blacklist;
    private final BlacklistEntry entry;

    public LogAction(Blacklist blacklist, BlacklistEntry entry) {
        Preconditions.checkNotNull((Object)blacklist);
        Preconditions.checkNotNull((Object)entry);
        this.blacklist = blacklist;
        this.entry = entry;
    }

    @Override
    protected ActionResult applyNonRepeated(BlacklistEvent event, boolean silent) {
        if (silent) {
            return ActionResult.INHERIT;
        }
        this.blacklist.getLogger().logEvent(event, this.entry.getComment());
        return ActionResult.INHERIT;
    }
}

