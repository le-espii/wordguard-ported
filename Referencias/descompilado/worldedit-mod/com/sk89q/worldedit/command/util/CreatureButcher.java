/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.util;

import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.EntityFunction;

public class CreatureButcher {
    private final Actor player;
    public int flags = 0;

    public CreatureButcher(Actor player) {
        this.player = player;
    }

    public void or(int flag, boolean on) {
        if (on) {
            this.flags |= flag;
        }
    }

    public void or(int flag, boolean on, String permission) {
        this.or(flag, on);
        if ((this.flags & flag) != 0 && !this.player.hasPermission(permission)) {
            this.flags &= ~flag;
        }
    }

    public EntityFunction createFunction() {
        return entity -> {
            boolean killPets = (this.flags & 1) != 0;
            boolean killNPCs = (this.flags & 2) != 0;
            boolean killAnimals = (this.flags & 4) != 0;
            boolean killGolems = (this.flags & 8) != 0;
            boolean killAmbient = (this.flags & 0x10) != 0;
            boolean killTagged = (this.flags & 0x20) != 0;
            boolean killArmorStands = (this.flags & 0x40) != 0;
            boolean killWaterCreatures = (this.flags & 0x80) != 0;
            EntityProperties type = entity.getFacet(EntityProperties.class);
            if (type == null) {
                return false;
            }
            if (type.isPlayerDerived()) {
                return false;
            }
            if (!type.isLiving()) {
                return false;
            }
            if (!killAnimals && type.isAnimal()) {
                return false;
            }
            if (!killPets && type.isTamed()) {
                return false;
            }
            if (!killGolems && type.isGolem()) {
                return false;
            }
            if (!killNPCs && type.isNPC()) {
                return false;
            }
            if (!killAmbient && type.isAmbient()) {
                return false;
            }
            if (!killTagged && type.isTagged()) {
                return false;
            }
            if (!killArmorStands && type.isArmorStand()) {
                return false;
            }
            if (!killWaterCreatures && type.isWaterCreature()) {
                return false;
            }
            entity.remove();
            return true;
        };
    }

    public final class Flags {
        public static final int PETS = 1;
        public static final int NPCS = 2;
        public static final int ANIMALS = 4;
        public static final int GOLEMS = 8;
        public static final int AMBIENT = 16;
        public static final int TAGGED = 32;
        public static final int ARMOR_STAND = 64;
        public static final int WATER = 128;
        public static final int FRIENDLY = 191;

        private Flags(CreatureButcher this$0) {
        }
    }
}

