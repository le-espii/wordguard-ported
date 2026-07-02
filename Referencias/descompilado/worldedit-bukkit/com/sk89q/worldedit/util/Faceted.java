/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util;

import javax.annotation.Nullable;

public interface Faceted {
    @Nullable
    public <T> T getFacet(Class<? extends T> var1);
}

