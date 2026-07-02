/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.legacycompat;

import com.sk89q.worldedit.extent.clipboard.io.legacycompat.NBTCompatibilityHandler;
import com.sk89q.worldedit.registry.state.IntegerProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinTagType;

public class NoteBlockCompatibilityHandler
implements NBTCompatibilityHandler {
    private static final IntegerProperty NOTE_PROPERTY;

    @Override
    public BaseBlock updateNbt(BaseBlock block) {
        if (NOTE_PROPERTY == null || block.getBlockType() != BlockTypes.NOTE_BLOCK) {
            return block;
        }
        LinCompoundTag tag = block.getNbt();
        if (tag == null) {
            return block;
        }
        LinByteTag noteTag = tag.findTag("note", LinTagType.byteTag());
        if (noteTag == null) {
            return block;
        }
        return ((BaseBlock)block.with((Property)NOTE_PROPERTY, (Object)noteTag.valueAsByte())).toBaseBlock();
    }

    static {
        IntegerProperty temp;
        try {
            temp = (IntegerProperty)BlockTypes.NOTE_BLOCK.getProperty("note");
        }
        catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
            temp = null;
        }
        NOTE_PROPERTY = temp;
    }
}

