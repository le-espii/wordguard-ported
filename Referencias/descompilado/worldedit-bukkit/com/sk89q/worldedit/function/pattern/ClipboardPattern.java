/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.pattern;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.pattern.RepeatingExtentPattern;
import com.sk89q.worldedit.math.BlockVector3;

public class ClipboardPattern
extends RepeatingExtentPattern {
    public ClipboardPattern(Clipboard clipboard) {
        this(clipboard, BlockVector3.ZERO);
    }

    public ClipboardPattern(Clipboard clipboard, BlockVector3 offset) {
        super(clipboard, clipboard.getMinimumPoint(), offset);
    }
}

