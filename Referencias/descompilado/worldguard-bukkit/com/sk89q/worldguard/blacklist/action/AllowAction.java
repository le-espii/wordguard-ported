/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.action;

import com.sk89q.worldguard.blacklist.action.Action;
import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public final class AllowAction
implements Action {
    private static final AllowAction INSTANCE = new AllowAction();

    private AllowAction() {
    }

    @Override
    public ActionResult apply(BlacklistEvent event, boolean silent, boolean repeating, boolean forceRepeat) {
        if (silent) {
            return ActionResult.ALLOW_OVERRIDE;
        }
        return ActionResult.ALLOW;
    }

    public static AllowAction getInstance() {
        return INSTANCE;
    }
}

