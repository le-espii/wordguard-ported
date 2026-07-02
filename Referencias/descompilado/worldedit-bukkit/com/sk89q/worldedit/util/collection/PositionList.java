/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.collection;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.LongPositionList;
import com.sk89q.worldedit.util.collection.VectorPositionList;
import java.util.Iterator;

interface PositionList {
    public static PositionList create(boolean extendedYLimit) {
        if (extendedYLimit) {
            return new VectorPositionList();
        }
        return new LongPositionList();
    }

    public BlockVector3 get(int var1);

    public void add(BlockVector3 var1);

    public int size();

    public void clear();

    public Iterator<BlockVector3> iterator();

    public Iterator<BlockVector3> reverseIterator();
}

