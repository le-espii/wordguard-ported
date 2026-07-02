/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.io.file;

import java.io.Closeable;
import java.nio.file.Path;

public interface ArchiveDir
extends Closeable {
    public Path getPath();
}

