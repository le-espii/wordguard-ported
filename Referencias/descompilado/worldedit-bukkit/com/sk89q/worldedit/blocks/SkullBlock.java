/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.blocks;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.blocks.LegacyBaseBlockWrapper;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.world.block.BlockState;
import java.util.HashMap;

@Deprecated
public class SkullBlock
extends LegacyBaseBlockWrapper {
    private String owner = "";

    public SkullBlock(BlockState state) {
        super(state);
        this.owner = "";
    }

    public SkullBlock(BlockState blockState, String owner) {
        super(blockState);
        this.setOwner(owner);
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? "" : (owner.length() > 16 || owner.isEmpty() ? "" : owner);
    }

    public String getOwner() {
        return this.owner;
    }

    @Override
    @Deprecated
    public boolean hasNbtData() {
        return true;
    }

    @Override
    public String getNbtId() {
        return "skull";
    }

    @Override
    @Deprecated
    public CompoundTag getNbtData() {
        HashMap values = new HashMap();
        HashMap inner = new HashMap();
        inner.put("Name", new StringTag(this.owner));
        values.put(DeprecationUtil.getHeadOwnerKey(), new CompoundTag(inner));
        return new CompoundTag(values);
    }

    @Override
    @Deprecated
    public void setNbtData(CompoundTag rootTag) {
        if (rootTag == null) {
            return;
        }
        Object values = rootTag.getValue();
        Tag t = (Tag)values.get("id");
        if (!(t instanceof StringTag) || !((StringTag)t).getValue().equals(this.getNbtId())) {
            throw new RuntimeException(String.format("'%s' tile entity expected", this.getNbtId()));
        }
        t = (Tag)values.get(DeprecationUtil.getHeadOwnerKey());
        if (t instanceof CompoundTag) {
            this.setOwner(((Tag)((CompoundTag)t).getValue().get("Name")).getValue().toString());
        }
    }
}

