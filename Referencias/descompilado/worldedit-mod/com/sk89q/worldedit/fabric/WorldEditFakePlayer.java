/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.class_1282
 *  net.minecraft.class_1306
 *  net.minecraft.class_1659
 *  net.minecraft.class_2561
 *  net.minecraft.class_3218
 *  net.minecraft.class_3222
 *  net.minecraft.class_3445
 *  net.minecraft.class_8791
 */
package com.sk89q.worldedit.fabric;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.class_1282;
import net.minecraft.class_1306;
import net.minecraft.class_1659;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3445;
import net.minecraft.class_8791;

public class WorldEditFakePlayer
extends class_3222 {
    private static final GameProfile FAKE_WORLDEDIT_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("worldedit".getBytes()), "[WorldEdit]");
    private static final class_8791 FAKE_CLIENT_INFO = new class_8791("en_US", 16, class_1659.field_7538, true, 0, class_1306.field_6182, false, false);

    public WorldEditFakePlayer(class_3218 world) {
        super(world.method_8503(), world, FAKE_WORLDEDIT_PROFILE, FAKE_CLIENT_INFO);
    }

    public void method_5773() {
    }

    public void method_7342(class_3445<?> stat, int incrementer) {
    }

    public void method_7259(class_3445<?> stat) {
    }

    public void method_7353(class_2561 message, boolean actionBar) {
        super.method_7353(message, actionBar);
    }

    public boolean method_5679(class_1282 damageSource) {
        return true;
    }
}

