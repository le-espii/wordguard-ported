/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.io.file;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class FilenameException
extends WorldEditException {
    private final String filename;

    public FilenameException(String filename) {
        this.filename = filename;
    }

    public FilenameException(String filename, Component msg) {
        super(msg);
        this.filename = filename;
    }

    @Deprecated
    public FilenameException(String filename, String msg) {
        super(msg);
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }
}

