/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.class_1297
 *  net.minecraft.class_1303
 *  net.minecraft.class_1308
 *  net.minecraft.class_1321
 *  net.minecraft.class_1421
 *  net.minecraft.class_1427
 *  net.minecraft.class_1429
 *  net.minecraft.class_1480
 *  net.minecraft.class_1510
 *  net.minecraft.class_1531
 *  net.minecraft.class_1533
 *  net.minecraft.class_1534
 *  net.minecraft.class_1540
 *  net.minecraft.class_1541
 *  net.minecraft.class_1542
 *  net.minecraft.class_1655
 *  net.minecraft.class_1657
 *  net.minecraft.class_1676
 *  net.minecraft.class_1688
 *  net.minecraft.class_1690
 *  net.minecraft.class_1915
 *  net.minecraft.class_3222
 */
package com.sk89q.worldedit.fabric;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import net.minecraft.class_1297;
import net.minecraft.class_1303;
import net.minecraft.class_1308;
import net.minecraft.class_1321;
import net.minecraft.class_1421;
import net.minecraft.class_1427;
import net.minecraft.class_1429;
import net.minecraft.class_1480;
import net.minecraft.class_1510;
import net.minecraft.class_1531;
import net.minecraft.class_1533;
import net.minecraft.class_1534;
import net.minecraft.class_1540;
import net.minecraft.class_1541;
import net.minecraft.class_1542;
import net.minecraft.class_1655;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_1688;
import net.minecraft.class_1690;
import net.minecraft.class_1915;
import net.minecraft.class_3222;

public class FabricEntityProperties
implements EntityProperties {
    private final class_1297 entity;

    public FabricEntityProperties(class_1297 entity) {
        Preconditions.checkNotNull((Object)entity);
        this.entity = entity;
    }

    @Override
    public boolean isPlayerDerived() {
        return this.entity instanceof class_1657;
    }

    @Override
    public boolean isProjectile() {
        return this.entity instanceof class_1676;
    }

    @Override
    public boolean isItem() {
        return this.entity instanceof class_1542;
    }

    @Override
    public boolean isFallingBlock() {
        return this.entity instanceof class_1540;
    }

    @Override
    public boolean isPainting() {
        return this.entity instanceof class_1534;
    }

    @Override
    public boolean isItemFrame() {
        return this.entity instanceof class_1533;
    }

    @Override
    public boolean isBoat() {
        return this.entity instanceof class_1690;
    }

    @Override
    public boolean isMinecart() {
        return this.entity instanceof class_1688;
    }

    @Override
    public boolean isTNT() {
        return this.entity instanceof class_1541;
    }

    @Override
    public boolean isExperienceOrb() {
        return this.entity instanceof class_1303;
    }

    @Override
    public boolean isLiving() {
        return this.entity instanceof class_1308;
    }

    @Override
    public boolean isAnimal() {
        return this.entity instanceof class_1429;
    }

    @Override
    public boolean isAmbient() {
        return this.entity instanceof class_1421;
    }

    @Override
    public boolean isNPC() {
        return this.entity instanceof class_1655 || this.entity instanceof class_1915;
    }

    @Override
    public boolean isGolem() {
        return this.entity instanceof class_1427;
    }

    @Override
    public boolean isTamed() {
        return this.entity instanceof class_1321 && ((class_1321)this.entity).method_6181();
    }

    @Override
    public boolean isTagged() {
        return this.entity.method_16914();
    }

    @Override
    public boolean isArmorStand() {
        return this.entity instanceof class_1531;
    }

    @Override
    public boolean isPasteable() {
        return !(this.entity instanceof class_3222) && !(this.entity instanceof class_1510);
    }

    @Override
    public boolean isWaterCreature() {
        return this.entity instanceof class_1480;
    }
}

