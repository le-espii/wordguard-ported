/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import java.io.Closeable;
import java.io.IOException;

public interface ClipboardWriter
extends Closeable {
    public void write(Clipboard var1) throws IOException;
}

