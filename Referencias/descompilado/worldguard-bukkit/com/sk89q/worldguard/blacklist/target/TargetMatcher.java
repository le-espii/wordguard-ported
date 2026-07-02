/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.target;

import com.sk89q.worldguard.blacklist.target.Target;

public interface TargetMatcher {
    public String getMatchedTypeId();

    public boolean test(Target var1);
}

