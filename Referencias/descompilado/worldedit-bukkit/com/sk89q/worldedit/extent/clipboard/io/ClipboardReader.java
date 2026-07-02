/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import java.io.Closeable;
import java.io.IOException;
import java.util.OptionalInt;

public interface ClipboardReader
extends Closeable {
    public Clipboard read() throws IOException;

    default public OptionalInt getDataVersion() {
        return OptionalInt.empty();
    }
}

