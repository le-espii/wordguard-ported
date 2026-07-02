/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.item;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.util.GuavaUtil;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.registry.ItemMaterial;
import javax.annotation.Nullable;

public class ItemType
implements Keyed {
    public static final NamespacedRegistry<ItemType> REGISTRY = new NamespacedRegistry("item type", true);
    private final String id;
    private final LazyReference<String> name = LazyReference.from(() -> {
        String name = GuavaUtil.firstNonNull(WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getItemRegistry().getName(this), "");
        return name.isEmpty() ? this.id() : name;
    });
    private final LazyReference<Component> richName = LazyReference.from(() -> WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getItemRegistry().getRichName(this));
    private final LazyReference<ItemMaterial> itemMaterial = LazyReference.from(() -> WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getItemRegistry().getMaterial(this));

    public ItemType(String id) {
        if (!((String)id).contains(":")) {
            id = "minecraft:" + (String)id;
        }
        this.id = id;
    }

    @Override
    public String id() {
        return this.id;
    }

    public Component getRichName() {
        return this.richName.getValue();
    }

    @Deprecated
    public String getName() {
        return this.name.getValue();
    }

    public boolean hasBlockType() {
        return this.getBlockType() != null;
    }

    @Nullable
    public BlockType getBlockType() {
        return BlockTypes.get(this.id);
    }

    public ItemMaterial getMaterial() {
        return this.itemMaterial.getValue();
    }

    public String toString() {
        return this.id();
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof ItemType && this.id.equals(((ItemType)obj).id);
    }
}

