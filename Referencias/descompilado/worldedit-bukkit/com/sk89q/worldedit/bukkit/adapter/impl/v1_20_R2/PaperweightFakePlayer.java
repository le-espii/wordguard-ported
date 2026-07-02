/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.network.chat.IChatBaseComponent
 *  net.minecraft.server.level.ClientInformation
 *  net.minecraft.server.level.EntityPlayer
 *  net.minecraft.server.level.WorldServer
 *  net.minecraft.stats.Statistic
 *  net.minecraft.world.ITileInventory
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EnumMainHand
 *  net.minecraft.world.entity.player.EnumChatVisibility
 *  net.minecraft.world.level.block.entity.TileEntitySign
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 */
package com.sk89q.worldedit.bukkit.adapter.impl.v1_20_R2;

import com.mojang.authlib.GameProfile;
import java.util.OptionalInt;
import java.util.UUID;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.Statistic;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.event.player.PlayerTeleportEvent;

class PaperweightFakePlayer
extends EntityPlayer {
    private static final GameProfile FAKE_WORLDEDIT_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("worldedit".getBytes()), "[WorldEdit]");
    private static final Vec3D ORIGIN = new Vec3D(0.0, 0.0, 0.0);
    private static final ClientInformation FAKE_CLIENT_INFO = new ClientInformation("en_US", 16, EnumChatVisibility.a, true, 0, EnumMainHand.a, false, false);

    PaperweightFakePlayer(WorldServer world) {
        super(world.n(), world, FAKE_WORLDEDIT_PROFILE, FAKE_CLIENT_INFO);
    }

    public Vec3D dj() {
        return ORIGIN;
    }

    public void l() {
    }

    public void a(DamageSource damagesource) {
    }

    public Entity changeDimension(WorldServer worldserver, PlayerTeleportEvent.TeleportCause cause) {
        return this;
    }

    public OptionalInt a(ITileInventory factory) {
        return OptionalInt.empty();
    }

    public void a(ClientInformation clientOptions) {
    }

    public void a(IChatBaseComponent message, boolean actionBar) {
    }

    public void a(Statistic<?> stat, int amount) {
    }

    public void b(Statistic<?> stat) {
    }

    public boolean b(DamageSource damageSource) {
        return true;
    }

    public void a(TileEntitySign sign, boolean front) {
    }
}

