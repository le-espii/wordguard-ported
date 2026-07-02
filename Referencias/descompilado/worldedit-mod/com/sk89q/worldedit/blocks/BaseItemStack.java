/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.blocks;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.item.ItemType;
import org.enginehub.linbus.tree.LinCompoundTag;

public class BaseItemStack
extends BaseItem {
    private int amount = 1;

    public BaseItemStack(ItemType itemType) {
        super(itemType);
    }

    public BaseItemStack(ItemType itemType, int amount) {
        super(itemType);
        this.amount = amount;
    }

    @Deprecated
    public BaseItemStack(ItemType id, CompoundTag tag, int amount) {
        super(id, tag);
        this.amount = amount;
    }

    public BaseItemStack(ItemType id, LazyReference<LinCompoundTag> tag, int amount) {
        super(id, tag);
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Component getRichName() {
        return WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getItemRegistry().getRichName(this);
    }
}

