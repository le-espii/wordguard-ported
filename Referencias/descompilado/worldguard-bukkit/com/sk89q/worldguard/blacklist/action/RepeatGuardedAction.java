/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.action;

import com.sk89q.worldguard.blacklist.action.Action;
import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public abstract class RepeatGuardedAction
implements Action {
    @Override
    public final ActionResult apply(BlacklistEvent event, boolean silent, boolean repeating, boolean forceRepeat) {
        if (!repeating || forceRepeat) {
            return this.applyNonRepeated(event, silent);
        }
        return ActionResult.INHERIT;
    }

    protected abstract ActionResult applyNonRepeated(BlacklistEvent var1, boolean var2);
}

