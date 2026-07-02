/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.pattern;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractPattern;
import com.sk89q.worldedit.function.pattern.ExtentPattern;

public abstract class AbstractExtentPattern
extends AbstractPattern
implements ExtentPattern {
    private final Extent extent;

    public AbstractExtentPattern(Extent extent) {
        this.extent = extent;
        Preconditions.checkNotNull((Object)extent);
    }

    @Override
    public Extent getExtent() {
        return this.extent;
    }
}

