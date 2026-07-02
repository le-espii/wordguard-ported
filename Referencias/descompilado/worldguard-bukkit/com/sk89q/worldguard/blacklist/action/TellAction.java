/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.blacklist.action;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.blacklist.BlacklistEntry;
import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.action.RepeatGuardedAction;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public class TellAction
extends RepeatGuardedAction {
    private final BlacklistEntry entry;

    public TellAction(BlacklistEntry entry) {
        Preconditions.checkNotNull((Object)entry);
        this.entry = entry;
    }

    @Override
    protected ActionResult applyNonRepeated(BlacklistEvent event, boolean silent) {
        if (silent) {
            return ActionResult.INHERIT;
        }
        String message = this.entry.getMessage();
        if (event.getPlayer() != null) {
            if (message != null) {
                message = message.replaceAll("(?!<\\\\)\\\\n", "\n").replaceAll("\\\\\\\\n", "\\n");
                event.getPlayer().print(String.format(message, event.getTarget().getFriendlyName()));
            } else {
                event.getPlayer().printError("You're not allowed to " + event.getDescription() + " " + event.getTarget().getFriendlyName() + ".");
            }
        }
        return ActionResult.INHERIT;
    }
}

