/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.io.file;

import com.sk89q.worldedit.util.io.file.ArchiveDir;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface ArchiveNioSupport {
    public Optional<ArchiveDir> tryOpenAsDir(Path var1) throws IOException;
}

