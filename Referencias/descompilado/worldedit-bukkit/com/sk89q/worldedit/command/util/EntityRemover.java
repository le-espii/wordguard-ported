/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.function.EntityFunction;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class EntityRemover {
    private final Type type;

    public static EntityRemover fromString(String str) {
        Type type = Type.findByPattern(str);
        if (type != null) {
            return new EntityRemover(type);
        }
        throw new IllegalArgumentException("Acceptable types: projectiles, items, paintings, itemframes, boats, minecarts, tnt, xp, or all");
    }

    private EntityRemover(Type type) {
        this.type = type;
    }

    public EntityFunction createFunction() {
        Type type = this.type;
        Preconditions.checkNotNull((Object)((Object)type), (Object)"type can't be null");
        return entity -> {
            EntityProperties registryType = entity.getFacet(EntityProperties.class);
            if (registryType != null && type.matches(registryType)) {
                entity.remove();
                return true;
            }
            return false;
        };
    }

    public static enum Type {
        ALL("all"){

            @Override
            boolean matches(EntityProperties type) {
                for (Type value : 1.values()) {
                    if (value == this || !value.matches(type)) continue;
                    return true;
                }
                return false;
            }
        }
        ,
        PROJECTILES("projectiles?|arrows?"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isProjectile();
            }
        }
        ,
        ITEMS("items?|drops?"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isItem();
            }
        }
        ,
        FALLING_BLOCKS("falling(blocks?|sand|gravel)"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isFallingBlock();
            }
        }
        ,
        PAINTINGS("paintings?|art"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isPainting();
            }
        }
        ,
        ITEM_FRAMES("(item)frames?"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isItemFrame();
            }
        }
        ,
        BOATS("boats?"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isBoat();
            }
        }
        ,
        MINECARTS("(mine)?carts?"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isMinecart();
            }
        }
        ,
        TNT("tnt"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isTNT();
            }
        }
        ,
        XP_ORBS("xp"){

            @Override
            boolean matches(EntityProperties type) {
                return type.isExperienceOrb();
            }
        };

        private final Pattern pattern;

        private Type(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        public boolean matches(String str) {
            return this.pattern.matcher(str).matches();
        }

        abstract boolean matches(EntityProperties var1);

        @Nullable
        public static Type findByPattern(String str) {
            for (Type type : Type.values()) {
                if (!type.matches(str)) continue;
                return type;
            }
            return null;
        }
    }
}

