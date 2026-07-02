/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.registry;

import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;

public interface Keyed {
    @Deprecated(forRemoval=true)
    default public String getId() {
        return this.id();
    }

    @NonAbstractForCompatibility(delegateName="getId", delegateParams={})
    default public String id() {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.getId();
    }
}

