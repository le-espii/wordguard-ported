/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.block.BlockState
 *  com.sk89q.worldedit.world.block.BlockTypes
 */
package com.sk89q.worldguard.chest;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.chest.ChestProtection;

public abstract class SignChestProtection
implements ChestProtection {
    public abstract Boolean isProtectedSign(Location var1, LocalPlayer var2);

    @Override
    public boolean isProtected(Location location, LocalPlayer player) {
        BlockState blockState = location.getExtent().getBlock(location.toVector().toBlockPoint());
        if (this.isChest(blockState.getBlockType())) {
            return this.isProtectedSignAround(location.setY(location.getY() - 1.0), player);
        }
        if (blockState.getBlockType() == BlockTypes.SIGN) {
            return this.isProtectedSignAndChestBinary(location, player);
        }
        Boolean res = this.isProtectedSign(location.setY(location.getY() + 1.0), player);
        if (res != null) {
            return res;
        }
        return false;
    }

    @Override
    public boolean isProtectedPlacement(Location block, LocalPlayer player) {
        return this.isProtectedSignAround(block, player);
    }

    private boolean isProtectedSignAround(Location searchBlock, LocalPlayer player) {
        Location side = searchBlock;
        Boolean res = this.isProtectedSign(side, player);
        if (res != null && res.booleanValue()) {
            return res;
        }
        side = searchBlock.setX(searchBlock.getX() - 1.0);
        res = this.isProtectedSignAndChest(side, player);
        if (res != null && res.booleanValue()) {
            return res;
        }
        side = searchBlock.setX(searchBlock.getX() + 1.0);
        res = this.isProtectedSignAndChest(side, player);
        if (res != null && res.booleanValue()) {
            return res;
        }
        side = searchBlock.setZ(searchBlock.getZ() - 1.0);
        res = this.isProtectedSignAndChest(side, player);
        if (res != null && res.booleanValue()) {
            return res;
        }
        side = searchBlock.setZ(searchBlock.getZ() + 1.0);
        res = this.isProtectedSignAndChest(side, player);
        if (res != null && res.booleanValue()) {
            return res;
        }
        return false;
    }

    private Boolean isProtectedSignAndChest(Location block, LocalPlayer player) {
        if (!this.isChest(block.getExtent().getBlock(block.setY(block.getY() + 1.0).toVector().toBlockPoint()).getBlockType())) {
            return null;
        }
        return this.isProtectedSign(block, player);
    }

    private boolean isProtectedSignAndChestBinary(Location block, LocalPlayer player) {
        Boolean res = this.isProtectedSignAndChest(block, player);
        return res != null && res != false;
    }

    @Override
    public boolean isAdjacentChestProtected(Location searchBlock, LocalPlayer player) {
        Location side = searchBlock;
        boolean res = this.isProtected(side, player);
        if (res) {
            return res;
        }
        side = searchBlock.setX(searchBlock.getX() - 1.0);
        res = this.isProtected(side, player);
        if (res) {
            return res;
        }
        side = searchBlock.setX(searchBlock.getX() + 1.0);
        res = this.isProtected(side, player);
        if (res) {
            return res;
        }
        side = searchBlock.setZ(searchBlock.getZ() - 1.0);
        res = this.isProtected(side, player);
        if (res) {
            return res;
        }
        side = searchBlock.setZ(searchBlock.getZ() + 1.0);
        res = this.isProtected(side, player);
        if (res) {
            return res;
        }
        return false;
    }
}

