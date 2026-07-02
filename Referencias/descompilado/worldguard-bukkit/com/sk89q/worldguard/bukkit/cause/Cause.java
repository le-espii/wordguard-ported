/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.block.Block
 *  org.bukkit.entity.AnimalTamer
 *  org.bukkit.entity.AreaEffectCloud
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Firework
 *  org.bukkit.entity.LightningStrike
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Tameable
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.metadata.Metadatable
 *  org.bukkit.projectiles.BlockProjectileSource
 *  org.bukkit.projectiles.ProjectileSource
 */
package com.sk89q.worldguard.bukkit.cause;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.internal.WGMetadata;
import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.bukkit.util.Entities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.metadata.Metadatable;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public final class Cause {
    private static final String CAUSE_KEY = "worldguard.cause";
    private static final Cause UNKNOWN = new Cause(Collections.emptyList(), false);
    private final List<Object> causes;
    private final boolean indirect;

    private Cause(List<Object> causes, boolean indirect) {
        Preconditions.checkNotNull(causes);
        this.causes = causes;
        this.indirect = indirect;
    }

    public boolean isIndirect() {
        return this.indirect;
    }

    public boolean isKnown() {
        Tameable tameable;
        Object object = this.getRootCause();
        if (object == null) {
            return false;
        }
        if (object instanceof Tameable && (tameable = (Tameable)object).isTamed()) {
            return false;
        }
        if (object instanceof TNTPrimed || object instanceof Vehicle) {
            if (!PaperLib.isPaper()) {
                return false;
            }
            Entity entity = (Entity)object;
            BukkitWorldConfiguration config = WorldGuardPlugin.inst().getConfigManager().get(entity.getWorld().getName());
            if (!config.usePaperEntityOrigin || entity.getOrigin() == null) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public Object getRootCause() {
        if (!this.causes.isEmpty()) {
            return this.causes.get(0);
        }
        return null;
    }

    @Nullable
    public Player getFirstPlayer() {
        for (Object object : this.causes) {
            Player p;
            if (!(object instanceof Player) || Entities.isNPC((Entity)(p = (Player)object))) continue;
            return p;
        }
        return null;
    }

    @Nullable
    public Entity getFirstEntity() {
        for (Object object : this.causes) {
            if (!(object instanceof Entity)) continue;
            Entity e = (Entity)object;
            return e;
        }
        return null;
    }

    @Nullable
    public Entity getFirstNonPlayerEntity() {
        for (Object object : this.causes) {
            if (!(object instanceof Entity)) continue;
            Entity e = (Entity)object;
            if (object instanceof Player && !Entities.isNPC(e)) continue;
            return e;
        }
        return null;
    }

    @Nullable
    public Block getFirstBlock() {
        for (Object object : this.causes) {
            if (!(object instanceof Block)) continue;
            Block b = (Block)object;
            return b;
        }
        return null;
    }

    @Nullable
    public EntityType find(EntityType ... types) {
        for (Object object : this.causes) {
            if (!(object instanceof Entity)) continue;
            for (EntityType type : types) {
                if (((Entity)object).getType() != type) continue;
                return type;
            }
        }
        return null;
    }

    public String toString() {
        return Joiner.on((String)" | ").join(this.causes);
    }

    public static Cause create(Object ... cause) {
        if (cause != null) {
            Builder builder = new Builder(cause.length);
            builder.addAll(cause);
            return builder.build();
        }
        return UNKNOWN;
    }

    public static Cause unknown() {
        return UNKNOWN;
    }

    public static void trackParentCause(Metadatable target, Object parent) {
        if (target instanceof Block) {
            throw new IllegalArgumentException("Can't track causes on Blocks because Cause doesn't check block metadata");
        }
        WGMetadata.put(target, CAUSE_KEY, parent);
    }

    public static void untrackParentCause(Metadatable target) {
        WGMetadata.remove(target, CAUSE_KEY);
    }

    private static final class Builder {
        private final List<Object> causes;
        private final Set<Object> seen = Sets.newHashSet();
        private boolean indirect;

        private Builder(int expectedSize) {
            this.causes = new ArrayList<Object>(expectedSize);
        }

        private void addAll(Object ... element) {
            if (element == null) {
                return;
            }
            for (Object o : element) {
                if (o == null || this.seen.contains(o)) continue;
                this.seen.add(o);
                if (o instanceof TNTPrimed) {
                    this.addAll(((TNTPrimed)o).getSource());
                } else if (o instanceof Projectile) {
                    Entity spawningEntity;
                    UUID spawningUUID;
                    ProjectileSource shooter = ((Projectile)o).getShooter();
                    this.addAll(shooter);
                    if (shooter == null && o instanceof Firework && PaperLib.isPaper() && (spawningUUID = ((Firework)o).getSpawningEntity()) != null && (spawningEntity = Bukkit.getEntity((UUID)spawningUUID)) != null) {
                        this.addAll(spawningEntity);
                    }
                } else if (o instanceof Vehicle) {
                    ((Vehicle)o).getPassengers().forEach(xva$0 -> this.addAll(xva$0));
                } else if (o instanceof AreaEffectCloud) {
                    this.indirect = true;
                    this.addAll(((AreaEffectCloud)o).getSource());
                } else if (o instanceof Tameable) {
                    Tameable tameable = (Tameable)o;
                    this.indirect = true;
                    if (PaperLib.isPaper()) {
                        Player owner;
                        UUID ownerId = tameable.getOwnerUniqueId();
                        if (ownerId != null && (owner = Bukkit.getPlayer((UUID)ownerId)) != null) {
                            this.addAll(owner);
                        }
                    } else {
                        AnimalTamer owner = tameable.getOwner();
                        if (owner instanceof OfflinePlayer) {
                            OfflinePlayer player = (OfflinePlayer)owner;
                            this.addAll(player.getPlayer());
                        }
                    }
                } else if (o instanceof Creature && ((Creature)o).getTarget() != null) {
                    this.indirect = true;
                    this.addAll(((Creature)o).getTarget());
                } else if (o instanceof BlockProjectileSource) {
                    this.addAll(((BlockProjectileSource)o).getBlock());
                } else if (o instanceof LightningStrike && PaperLib.isPaper() && ((LightningStrike)o).getCausingEntity() != null) {
                    this.indirect = true;
                    this.addAll(((LightningStrike)o).getCausingEntity());
                }
                Object source = o;
                int index = this.causes.size();
                while (source instanceof Metadatable && !(source instanceof Block)) {
                    if ((source = WGMetadata.getIfPresent((Metadatable)source, Cause.CAUSE_KEY, Object.class)) == null) continue;
                    this.causes.add(index, source);
                    this.seen.add(source);
                }
                this.causes.add(o);
            }
        }

        public Cause build() {
            return new Cause(this.causes, this.indirect);
        }
    }
}

