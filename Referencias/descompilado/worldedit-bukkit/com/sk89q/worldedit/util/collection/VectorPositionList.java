/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 */
package com.sk89q.worldedit.util.collection;

import com.google.common.collect.AbstractIterator;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntArrayList;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntIterator;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntList;
import com.sk89q.worldedit.bukkit.fastutil.ints.IntListIterator;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.PositionList;
import java.util.Iterator;

class VectorPositionList
implements PositionList {
    private final IntList delegateX = new IntArrayList();
    private final IntList delegateY = new IntArrayList();
    private final IntList delegateZ = new IntArrayList();

    VectorPositionList() {
    }

    @Override
    public BlockVector3 get(int index) {
        return BlockVector3.at(this.delegateX.getInt(index), this.delegateY.getInt(index), this.delegateZ.getInt(index));
    }

    @Override
    public void add(BlockVector3 vector) {
        this.delegateX.add(vector.x());
        this.delegateY.add(vector.y());
        this.delegateZ.add(vector.z());
    }

    @Override
    public int size() {
        return this.delegateX.size();
    }

    @Override
    public void clear() {
        this.delegateX.clear();
        this.delegateY.clear();
        this.delegateZ.clear();
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return new AbstractIterator<BlockVector3>(){
            private final IntIterator iteratorX;
            private final IntIterator iteratorY;
            private final IntIterator iteratorZ;
            {
                this.iteratorX = VectorPositionList.this.delegateX.iterator();
                this.iteratorY = VectorPositionList.this.delegateY.iterator();
                this.iteratorZ = VectorPositionList.this.delegateZ.iterator();
            }

            protected BlockVector3 computeNext() {
                if (!this.iteratorX.hasNext()) {
                    return (BlockVector3)((Object)this.endOfData());
                }
                return BlockVector3.at(this.iteratorX.nextInt(), this.iteratorY.nextInt(), this.iteratorZ.nextInt());
            }
        };
    }

    @Override
    public Iterator<BlockVector3> reverseIterator() {
        return new AbstractIterator<BlockVector3>(){
            private final IntListIterator iteratorX;
            private final IntListIterator iteratorY;
            private final IntListIterator iteratorZ;
            {
                this.iteratorX = VectorPositionList.this.delegateX.listIterator(VectorPositionList.this.delegateX.size());
                this.iteratorY = VectorPositionList.this.delegateY.listIterator(VectorPositionList.this.delegateY.size());
                this.iteratorZ = VectorPositionList.this.delegateZ.listIterator(VectorPositionList.this.delegateZ.size());
            }

            protected BlockVector3 computeNext() {
                if (!this.iteratorX.hasPrevious()) {
                    return (BlockVector3)((Object)this.endOfData());
                }
                return BlockVector3.at(this.iteratorX.previousInt(), this.iteratorY.previousInt(), this.iteratorZ.previousInt());
            }
        };
    }
}

