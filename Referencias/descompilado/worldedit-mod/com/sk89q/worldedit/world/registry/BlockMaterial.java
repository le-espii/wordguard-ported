/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.registry;

public interface BlockMaterial {
    public boolean isAir();

    public boolean isFullCube();

    public boolean isOpaque();

    public boolean isPowerSource();

    public boolean isLiquid();

    public boolean isSolid();

    public float getHardness();

    public float getResistance();

    public float getSlipperiness();

    public int getLightValue();

    public boolean isFragileWhenPushed();

    public boolean isUnpushable();

    public boolean isTicksRandomly();

    public boolean isMovementBlocker();

    public boolean isBurnable();

    public boolean isToolRequired();

    public boolean isReplacedDuringPlacement();

    public boolean isTranslucent();

    public boolean hasContainer();
}

