/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ClientInformation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.stats.Stat
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.ChatVisiblity
 */
package com.sk89q.worldedit.neoforge;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public class WorldEditFakePlayer
extends ServerPlayer {
    private static final GameProfile FAKE_WORLDEDIT_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("worldedit".getBytes()), "[WorldEdit]");
    private static final ClientInformation FAKE_CLIENT_INFO = new ClientInformation("en_US", 16, ChatVisiblity.FULL, true, 0, HumanoidArm.LEFT, false, false);

    public WorldEditFakePlayer(ServerLevel world) {
        super(world.getServer(), world, FAKE_WORLDEDIT_PROFILE, FAKE_CLIENT_INFO);
    }

    public void tick() {
    }

    public void awardStat(Stat<?> stat, int incrementer) {
    }

    public void awardStat(Stat<?> stat) {
    }

    public void displayClientMessage(Component message, boolean actionBar) {
        super.displayClientMessage(message, actionBar);
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }
}

