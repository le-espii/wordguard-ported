/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.class_2680
 *  net.minecraft.class_3619
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.PassthroughBlockMaterial;
import javax.annotation.Nullable;
import net.minecraft.class_2680;
import net.minecraft.class_3619;

public class FabricBlockMaterial
extends PassthroughBlockMaterial {
    private final class_2680 block;

    public FabricBlockMaterial(class_2680 block, @Nullable BlockMaterial secondary) {
        super(secondary);
        this.block = block;
    }

    @Override
    public boolean isAir() {
        return this.block.method_26215() || super.isAir();
    }

    @Override
    public boolean isOpaque() {
        return this.block.method_26225();
    }

    @Override
    public boolean isLiquid() {
        return this.block.method_51176();
    }

    @Override
    public boolean isSolid() {
        return this.block.method_51367();
    }

    @Override
    public boolean isFragileWhenPushed() {
        return this.block.method_26223() == class_3619.field_15971;
    }

    @Override
    public boolean isUnpushable() {
        return this.block.method_26223() == class_3619.field_15972;
    }

    @Override
    public boolean isMovementBlocker() {
        return this.block.method_51366();
    }

    @Override
    public boolean isBurnable() {
        return this.block.method_50011();
    }

    @Override
    public boolean isToolRequired() {
        return this.block.method_29291();
    }

    @Override
    public boolean isReplacedDuringPlacement() {
        return this.block.method_45474();
    }
}

