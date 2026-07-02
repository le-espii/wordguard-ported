/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.blocks;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.NbtValued;
import com.sk89q.worldedit.world.item.ItemType;
import javax.annotation.Nullable;
import org.enginehub.linbus.format.snbt.LinStringIO;
import org.enginehub.linbus.tree.LinCompoundTag;

public class BaseItem
implements NbtValued {
    private ItemType itemType;
    @Nullable
    private LazyReference<LinCompoundTag> nbtData;

    public BaseItem(ItemType itemType) {
        Preconditions.checkNotNull((Object)itemType);
        this.itemType = itemType;
    }

    @Deprecated
    public BaseItem(ItemType itemType, @Nullable CompoundTag nbtData) {
        this(itemType, nbtData == null ? null : LazyReference.from(nbtData::toLinTag));
    }

    public BaseItem(ItemType itemType, @Nullable LazyReference<LinCompoundTag> tag) {
        Preconditions.checkNotNull((Object)itemType);
        this.itemType = itemType;
        this.nbtData = tag;
    }

    public ItemType getType() {
        return this.itemType;
    }

    public void setType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    @Nullable
    public LazyReference<LinCompoundTag> getNbtReference() {
        return this.nbtData;
    }

    @Override
    public void setNbtReference(@Nullable LazyReference<LinCompoundTag> nbtData) {
        this.nbtData = nbtData;
    }

    public String toString() {
        String nbtString = "";
        if (this.nbtData != null) {
            nbtString = LinStringIO.writeToString(this.nbtData.getValue());
        }
        return this.getType().id() + nbtString;
    }
}

