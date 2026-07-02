/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.share;

import com.sk89q.worldedit.WorldEditException;
import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface ShareOutputProvider {
    public void writeTo(OutputStream var1) throws IOException, WorldEditException;
}

