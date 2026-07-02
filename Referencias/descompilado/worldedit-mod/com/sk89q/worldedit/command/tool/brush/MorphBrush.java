/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;

public class MorphBrush
implements Brush {
    private final int minErodeFaces;
    private final int numErodeIterations;
    private final int minDilateFaces;
    private final int minDilateIterations;

    public MorphBrush(int minErodeFaces, int numErodeIterations, int minDilateFaces, int minDilateIterations) {
        this.minErodeFaces = minErodeFaces;
        this.numErodeIterations = numErodeIterations;
        this.minDilateFaces = minDilateFaces;
        this.minDilateIterations = minDilateIterations;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        editSession.morph(position, size, this.minErodeFaces, this.numErodeIterations, this.minDilateFaces, this.minDilateIterations);
    }
}

