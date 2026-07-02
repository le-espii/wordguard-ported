/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.util.GuavaUtil;
import com.sk89q.worldedit.world.registry.ItemMaterial;
import com.sk89q.worldedit.world.registry.SimpleItemMaterial;
import javax.annotation.Nullable;

public class PassthroughItemMaterial
implements ItemMaterial {
    private static final ItemMaterial DEFAULT_MATERIAL = new SimpleItemMaterial(0, 0);
    private final ItemMaterial itemMaterial;

    public PassthroughItemMaterial(@Nullable ItemMaterial material) {
        this.itemMaterial = GuavaUtil.firstNonNull(material, DEFAULT_MATERIAL);
    }

    @Override
    public int maxStackSize() {
        return this.itemMaterial.maxStackSize();
    }

    @Override
    public int maxDamage() {
        return this.itemMaterial.maxDamage();
    }
}

