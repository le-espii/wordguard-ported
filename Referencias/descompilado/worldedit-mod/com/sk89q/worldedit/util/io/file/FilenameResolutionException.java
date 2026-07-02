/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.io.file;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.io.file.FilenameException;

public class FilenameResolutionException
extends FilenameException {
    public FilenameResolutionException(String filename) {
        super(filename);
    }

    public FilenameResolutionException(String filename, Component msg) {
        super(filename, msg);
    }

    @Deprecated
    public FilenameResolutionException(String filename, String msg) {
        super(filename, msg);
    }
}

