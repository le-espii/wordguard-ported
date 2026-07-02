/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.entity.AbstractVillager
 *  org.bukkit.entity.Ambient
 *  org.bukkit.entity.Animals
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Boat
 *  org.bukkit.entity.ComplexEntityPart
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.ExperienceOrb
 *  org.bukkit.entity.FallingBlock
 *  org.bukkit.entity.Golem
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Painting
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Tameable
 *  org.bukkit.entity.Villager
 *  org.bukkit.entity.WaterMob
 *  org.bukkit.entity.minecart.ExplosiveMinecart
 */
package com.sk89q.worldedit.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Golem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.minecart.ExplosiveMinecart;

class BukkitEntityProperties
implements EntityProperties {
    private static final boolean HAS_ABSTRACT_VILLAGER;
    private final Entity entity;

    BukkitEntityProperties(Entity entity) {
        Preconditions.checkNotNull((Object)entity);
        this.entity = entity;
    }

    @Override
    public boolean isPlayerDerived() {
        return this.entity instanceof HumanEntity;
    }

    @Override
    public boolean isProjectile() {
        return this.entity instanceof Projectile;
    }

    @Override
    public boolean isItem() {
        return this.entity instanceof Item;
    }

    @Override
    public boolean isFallingBlock() {
        return this.entity instanceof FallingBlock;
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
        return this.entity instanceof Minecart;
    }

    @Override
    public boolean isTNT() {
        return this.entity instanceof TNTPrimed || this.entity instanceof ExplosiveMinecart;
    }

    @Override
    public boolean isExperienceOrb() {
        return this.entity instanceof ExperienceOrb;
    }

    @Override
    public boolean isLiving() {
        return this.entity instanceof LivingEntity;
    }

    @Override
    public boolean isAnimal() {
        return this.entity instanceof Animals;
    }

    @Override
    public boolean isAmbient() {
        return this.entity instanceof Ambient;
    }

    @Override
    public boolean isNPC() {
        if (HAS_ABSTRACT_VILLAGER) {
            return this.entity instanceof AbstractVillager;
        }
        return this.entity instanceof Villager;
    }

    @Override
    public boolean isGolem() {
        return this.entity instanceof Golem;
    }

    @Override
    public boolean isTamed() {
        return this.entity instanceof Tameable && ((Tameable)this.entity).isTamed();
    }

    @Override
    public boolean isTagged() {
        return this.entity instanceof LivingEntity && this.entity.getCustomName() != null;
    }

    @Override
    public boolean isArmorStand() {
        return this.entity instanceof ArmorStand;
    }

    @Override
    public boolean isPasteable() {
        return !(this.entity instanceof Player) && !(this.entity instanceof ComplexEntityPart);
    }

    @Override
    public boolean isWaterCreature() {
        return this.entity instanceof WaterMob;
    }

    static {
        boolean temp;
        try {
            Class.forName("org.bukkit.entity.AbstractVillager");
            temp = true;
        }
        catch (ClassNotFoundException e) {
            temp = false;
        }
        HAS_ABSTRACT_VILLAGER = temp;
    }
}

