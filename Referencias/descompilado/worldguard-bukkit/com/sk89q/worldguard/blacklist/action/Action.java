/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.action;

import com.sk89q.worldguard.blacklist.action.ActionResult;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public interface Action {
    public ActionResult apply(BlacklistEvent var1, boolean var2, boolean var3, boolean var4);
}

