/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.action;

import com.sk89q.worldguard.blacklist.action.Action;
import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public final class DenyAction
implements Action {
    private static final DenyAction INSTANCE = new DenyAction();

    private DenyAction() {
    }

    @Override
    public ActionResult apply(BlacklistEvent event, boolean silent, boolean repeating, boolean forceRepeat) {
        if (silent) {
            return ActionResult.DENY_OVERRIDE;
        }
        return ActionResult.DENY;
    }

    public static DenyAction getInstance() {
        return INSTANCE;
    }
}

