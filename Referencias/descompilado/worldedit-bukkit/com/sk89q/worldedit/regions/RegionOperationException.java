/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class RegionOperationException
extends WorldEditException {
    @Deprecated
    public RegionOperationException(String msg) {
        super(msg);
    }

    public RegionOperationException(Component msg) {
        super(msg);
    }
}

