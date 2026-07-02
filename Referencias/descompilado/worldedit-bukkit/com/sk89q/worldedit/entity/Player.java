/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.entity;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.gamemode.GameMode;
import javax.annotation.Nullable;

public interface Player
extends Entity,
Actor {
    public World getWorld();

    public boolean isHoldingPickAxe();

    public Direction getCardinalDirection(int var1);

    public BaseItemStack getItemInHand(HandSide var1);

    public BaseBlock getBlockInHand(HandSide var1) throws WorldEditException;

    public void giveItem(BaseItemStack var1);

    public BlockBag getInventoryBlockBag();

    public GameMode getGameMode();

    public void setGameMode(GameMode var1);

    public void findFreePosition(Location var1);

    public void setOnGround(Location var1);

    public void findFreePosition();

    public boolean ascendLevel();

    public boolean descendLevel();

    public boolean ascendToCeiling(int var1);

    public boolean ascendToCeiling(int var1, boolean var2);

    public boolean ascendUpwards(int var1);

    public boolean ascendUpwards(int var1, boolean var2);

    public void floatAt(int var1, int var2, int var3, boolean var4);

    @Deprecated
    default public Location getBlockIn() {
        return this.getBlockLocation();
    }

    public Location getBlockOn();

    public Location getBlockTrace(int var1, boolean var2);

    public Location getBlockTrace(int var1, boolean var2, @Nullable Mask var3);

    public Location getBlockTraceFace(int var1, boolean var2);

    public Location getBlockTraceFace(int var1, boolean var2, @Nullable Mask var3);

    public Location getBlockTrace(int var1);

    public Location getSolidBlockTrace(int var1);

    public Direction getCardinalDirection();

    public boolean passThroughForwardWall(int var1);

    @Deprecated
    default public void setPosition(Vector3 pos, float pitch, float yaw) {
        this.trySetPosition(pos, pitch, yaw);
    }

    @NonAbstractForCompatibility(delegateName="setPosition", delegateParams={Vector3.class, float.class, float.class})
    default public boolean trySetPosition(Vector3 pos, float pitch, float yaw) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        this.setPosition(pos, pitch, yaw);
        return true;
    }

    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 var1, @Nullable B var2);
}

