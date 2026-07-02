/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.ambient.AmbientCreature
 *  net.minecraft.world.entity.animal.AbstractGolem
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.animal.WaterAnimal
 *  net.minecraft.world.entity.boss.EnderDragonPart
 *  net.minecraft.world.entity.decoration.ArmorStand
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  net.minecraft.world.entity.decoration.Painting
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.item.PrimedTnt
 *  net.minecraft.world.entity.npc.Npc
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.item.trading.Merchant
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.trading.Merchant;

public class NeoForgeEntityProperties
implements EntityProperties {
    private final Entity entity;

    public NeoForgeEntityProperties(Entity entity) {
        Preconditions.checkNotNull((Object)entity);
        this.entity = entity;
    }

    @Override
    public boolean isPlayerDerived() {
        return this.entity instanceof Player;
    }

    @Override
    public boolean isProjectile() {
        return this.entity instanceof Projectile;
    }

    @Override
    public boolean isItem() {
        return this.entity instanceof ItemEntity;
    }

    @Override
    public boolean isFallingBlock() {
        return this.entity instanceof FallingBlockEntity;
    }

    @Override
    public boolean isPainting() {
        return this.entity instanceof Painting;
    }

    @Override
    public boolean isItemFrame() {
        return this.entity instanceof ItemFrame;
    }

    @Override
    public boolean isBoat() {
        return this.entity instanceof Boat;
    }

    @Override
    public boolean isMinecart() {
        return this.entity instanceof AbstractMinecart;
    }

    @Override
    public boolean isTNT() {
        return this.entity instanceof PrimedTnt;
    }

    @Override
    public boolean isExperienceOrb() {
        return this.entity instanceof ExperienceOrb;
    }

    @Override
    public boolean isLiving() {
        return this.entity instanceof Mob;
    }

    @Override
    public boolean isAnimal() {
        return this.entity instanceof Animal;
    }

    @Override
    public boolean isAmbient() {
        return this.entity instanceof AmbientCreature;
    }

    @Override
    public boolean isNPC() {
        return this.entity instanceof Npc || this.entity instanceof Merchant;
    }

    @Override
    public boolean isGolem() {
        return this.entity instanceof AbstractGolem;
    }

    @Override
    public boolean isTamed() {
        return this.entity instanceof TamableAnimal && ((TamableAnimal)this.entity).isTame();
    }

    @Override
    public boolean isTagged() {
        return this.entity.hasCustomName();
    }

    @Override
    public boolean isArmorStand() {
        return this.entity instanceof ArmorStand;
    }

    @Override
    public boolean isPasteable() {
        return !(this.entity instanceof ServerPlayer) && !(this.entity instanceof EnderDragonPart);
    }

    @Override
    public boolean isWaterCreature() {
        return this.entity instanceof WaterAnimal;
    }
}

