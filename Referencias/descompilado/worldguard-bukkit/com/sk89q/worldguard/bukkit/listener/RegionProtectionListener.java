/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.util.Location
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.ExperienceOrb
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Tameable
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.player.PlayerTakeLecternBookEvent
 *  org.bukkit.event.vehicle.VehicleExitEvent
 *  org.bukkit.metadata.Metadatable
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.DelegateEvent;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DestroyEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;
import com.sk89q.worldguard.bukkit.internal.WGMetadata;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.protection.events.DisallowedPVPEvent;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.bukkit.util.Events;
import com.sk89q.worldguard.bukkit.util.InteropUtils;
import com.sk89q.worldguard.bukkit.util.Materials;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.metadata.Metadatable;

public class RegionProtectionListener
extends AbstractListener {
    private static final String DENY_MESSAGE_KEY = "worldguard.region.lastMessage";
    private static final String DISEMBARK_MESSAGE_KEY = "worldguard.region.disembarkMessage";
    private static final int LAST_MESSAGE_DELAY = 500;

    public RegionProtectionListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    private void tellErrorMessage(DelegateEvent event, Cause cause, Location location, String what) {
        if (event.isSilent() || cause.isIndirect()) {
            return;
        }
        Object rootCause = cause.getRootCause();
        if (rootCause instanceof Player) {
            Player player = (Player)rootCause;
            long now = System.currentTimeMillis();
            Long lastTime = WGMetadata.getIfPresent((Metadatable)player, DENY_MESSAGE_KEY, Long.class);
            if (lastTime == null || now - lastTime >= 500L) {
                RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
                LocalPlayer localPlayer = RegionProtectionListener.getPlugin().wrapPlayer(player);
                String message = query.queryValue(BukkitAdapter.adapt((Location)location), localPlayer, Flags.DENY_MESSAGE);
                RegionProtectionListener.formatAndSendDenyMessage(what, localPlayer, message);
                WGMetadata.put((Metadatable)player, DENY_MESSAGE_KEY, now);
            }
        }
    }

    static void formatAndSendDenyMessage(String what, LocalPlayer localPlayer, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        message = WorldGuard.getInstance().getPlatform().getMatcher().replaceMacros((Actor)localPlayer, message);
        message = CommandUtils.replaceColorMacros(message);
        localPlayer.printRaw(message.replace("%what%", what));
    }

    private boolean isWhitelisted(Cause cause, World world, boolean pvp) {
        Object rootCause = cause.getRootCause();
        if (rootCause instanceof Player) {
            Player player = (Player)rootCause;
            BukkitWorldConfiguration config = RegionProtectionListener.getWorldConfig(world);
            if (config.fakePlayerBuildOverride && InteropUtils.isFakePlayer(player)) {
                return true;
            }
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            return !pvp && WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
        }
        return false;
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlaceBlock(PlaceBlockEvent event) {
        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }
        if (!RegionProtectionListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        if (this.isWhitelisted(event.getCause(), event.getWorld(), false)) {
            return;
        }
        Material type = event.getEffectiveMaterial();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        RegionAssociable associable = this.createRegionAssociable(event.getCause());
        if (event.getCause().getRootCause() instanceof Block && Materials.isLiquid(type) && !RegionProtectionListener.getWorldConfig((World)event.getWorld()).checkLiquidFlow) {
            return;
        }
        event.filter((Predicate)((com.google.common.base.Predicate)target -> {
            String what;
            boolean canPlace;
            if (Materials.isFire(type)) {
                Block block = event.getCause().getFirstBlock();
                boolean fire = block != null && Materials.isFire(block.getType());
                boolean lava = block != null && Materials.isLava(block.getType());
                ArrayList<StateFlag> flags = new ArrayList<StateFlag>();
                flags.add(Flags.BLOCK_PLACE);
                flags.add(Flags.LIGHTER);
                if (fire) {
                    flags.add(Flags.FIRE_SPREAD);
                }
                if (lava) {
                    flags.add(Flags.LAVA_FIRE);
                }
                canPlace = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, flags.toArray(new StateFlag[flags.size()])));
                what = "place fire";
            } else if (type == Material.FROSTED_ICE) {
                event.setSilent(true);
                canPlace = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.BLOCK_PLACE, Flags.FROSTED_ICE_FORM));
                what = "use frostwalker";
            } else {
                canPlace = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.BLOCK_PLACE));
                what = "place that block";
            }
            if (!canPlace) {
                this.tellErrorMessage(event, event.getCause(), (Location)target, what);
                return false;
            }
            return true;
        }));
    }

    @EventHandler(ignoreCancelled=true)
    public void onBreakBlock(BreakBlockEvent event) {
        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }
        if (!RegionProtectionListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        if (this.isWhitelisted(event.getCause(), event.getWorld(), false)) {
            return;
        }
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        if (!event.isCancelled()) {
            RegionAssociable associable = this.createRegionAssociable(event.getCause());
            event.filter((Predicate)((com.google.common.base.Predicate)target -> {
                String what;
                boolean canBreak;
                if (event.getCause().find(EntityType.TNT, EntityType.TNT_MINECART) != null) {
                    canBreak = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.BLOCK_BREAK, Flags.TNT));
                    what = "use dynamite";
                } else {
                    canBreak = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.BLOCK_BREAK));
                    what = "break that block";
                }
                if (!canBreak) {
                    this.tellErrorMessage(event, event.getCause(), (Location)target, what);
                    return false;
                }
                return true;
            }));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseBlock(UseBlockEvent event) {
        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }
        if (!RegionProtectionListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        if (this.isWhitelisted(event.getCause(), event.getWorld(), false)) {
            return;
        }
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        RegionAssociable associable = this.createRegionAssociable(event.getCause());
        event.filter((Predicate)((com.google.common.base.Predicate)target -> {
            String what;
            boolean canUse;
            Material type = target.getBlock().getType();
            if (Materials.isConsideredBuildingIfUsed(type)) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, new StateFlag[0]));
                what = "use that";
            } else if (Materials.isInventoryBlock(type)) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.CHEST_ACCESS));
                what = "open that";
            } else if (RegionProtectionListener.handleAsInventoryUsage(event.getOriginalEvent())) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.CHEST_ACCESS));
                what = "take that";
            } else if (Materials.isAnvil(type)) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.USE_ANVIL));
                what = "use that";
            } else if (Materials.isBed(type)) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.INTERACT, Flags.SLEEP));
                what = "sleep";
            } else if (type == Material.RESPAWN_ANCHOR) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.INTERACT, Flags.RESPAWN_ANCHORS));
                what = "use anchors";
            } else if (type == Material.TNT) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.INTERACT, Flags.TNT));
                what = "use explosives";
            } else if (Materials.isUseFlagApplicable(type)) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.INTERACT, Flags.USE));
                what = "use that";
            } else {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.INTERACT));
                what = "use that";
            }
            if (!canUse) {
                this.tellErrorMessage(event, event.getCause(), (Location)target, what);
                return false;
            }
            return true;
        }));
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnEntity(SpawnEntityEvent event) {
        String what;
        boolean canSpawn;
        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }
        if (!RegionProtectionListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        if (this.isWhitelisted(event.getCause(), event.getWorld(), false)) {
            return;
        }
        Location target = event.getTarget();
        EntityType type = event.getEffectiveType();
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        RegionAssociable associable = this.createRegionAssociable(event.getCause());
        if (Entities.isVehicle(type)) {
            canSpawn = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.PLACE_VEHICLE));
            what = "place vehicles";
        } else if (event.getEntity() instanceof Item) {
            canSpawn = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.ITEM_DROP));
            what = "drop items";
        } else if (type == EntityType.EXPERIENCE_ORB) {
            canSpawn = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.EXP_DROPS));
            what = "drop XP";
        } else if (Entities.isAoECloud(type)) {
            canSpawn = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.POTION_SPLASH));
            what = "use lingering potions";
        } else {
            canSpawn = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, new StateFlag[0]));
            what = "place things";
        }
        if (!canSpawn) {
            this.tellErrorMessage(event, event.getCause(), target, what);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onDestroyEntity(DestroyEntityEvent event) {
        String what;
        boolean canDestroy;
        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }
        if (!RegionProtectionListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        if (this.isWhitelisted(event.getCause(), event.getWorld(), false)) {
            return;
        }
        Location target = event.getTarget();
        EntityType type = event.getEntity().getType();
        RegionAssociable associable = this.createRegionAssociable(event.getCause());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        if (Entities.isVehicle(type)) {
            canDestroy = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.DESTROY_VEHICLE));
            what = "break vehicles";
        } else if (event.getEntity() instanceof Item || event.getEntity() instanceof ExperienceOrb) {
            canDestroy = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.ITEM_PICKUP));
            what = "pick up items";
        } else {
            canDestroy = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, new StateFlag[0]));
            what = "break things";
        }
        if (!canDestroy) {
            this.tellErrorMessage(event, event.getCause(), target, what);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseEntity(UseEntityEvent event) {
        String what;
        boolean canUse;
        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }
        if (!RegionProtectionListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        if (this.isWhitelisted(event.getCause(), event.getWorld(), false)) {
            return;
        }
        Location target = event.getTarget();
        RegionAssociable associable = this.createRegionAssociable(event.getCause());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Entity entity = event.getEntity();
        EntityType type = entity.getType();
        if (Entities.isHostile(entity) || Entities.isAmbient(entity) || Entities.isNPC(entity) || entity instanceof Player) {
            canUse = event.getRelevantFlags().isEmpty() || query.queryState(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, new StateFlag[0])) != StateFlag.State.DENY;
            what = "use that";
        } else if (Entities.isConsideredBuildingIfUsed(entity) || type == EntityType.CHEST_BOAT && event.getOriginalEvent() instanceof InventoryOpenEvent) {
            if ((type == EntityType.ITEM_FRAME || type == EntityType.GLOW_ITEM_FRAME) && event.getCause().getFirstPlayer() != null && ((ItemFrame)entity).getItem().getType() != Material.AIR) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.ITEM_FRAME_ROTATE));
                what = "change that";
            } else if (Entities.isMinecart(type) || type == EntityType.CHEST_BOAT) {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.CHEST_ACCESS));
                what = "open that";
            } else {
                canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, new StateFlag[0]));
                what = "change that";
            }
        } else if (Entities.isRiddenOnUse(entity)) {
            canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.RIDE, Flags.INTERACT));
            what = "ride that";
        } else {
            canUse = query.testBuild(BukkitAdapter.adapt((Location)target), associable, RegionProtectionListener.combine(event, Flags.INTERACT));
            what = "use that";
        }
        if (!canUse) {
            this.tellErrorMessage(event, event.getCause(), target, what);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onDamageEntity(DamageEntityEvent event) {
        String what;
        boolean canDamage;
        boolean pvp;
        if (event.getResult() == Event.Result.ALLOW) {
            return;
        }
        if (!RegionProtectionListener.isRegionSupportEnabled(event.getWorld())) {
            return;
        }
        com.sk89q.worldedit.util.Location target = BukkitAdapter.adapt((Location)event.getTarget());
        RegionAssociable associable = this.createRegionAssociable(event.getCause());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Player playerAttacker = event.getCause().getFirstPlayer();
        boolean bl = pvp = event.getEntity() instanceof Player && playerAttacker != null && !playerAttacker.equals((Object)event.getEntity());
        if (this.isWhitelisted(event.getCause(), event.getWorld(), pvp)) {
            return;
        }
        if (Entities.isHostile(event.getEntity()) || Entities.isAmbient(event.getEntity()) || Entities.isVehicle(event.getEntity().getType())) {
            canDamage = event.getRelevantFlags().isEmpty() || query.queryState(target, associable, RegionProtectionListener.combine(event, new StateFlag[0])) != StateFlag.State.DENY;
            what = "hit that";
        } else if (Entities.isConsideredBuildingIfUsed(event.getEntity())) {
            canDamage = query.testBuild(target, associable, RegionProtectionListener.combine(event, new StateFlag[0]));
            what = "change that";
        } else if (pvp) {
            LocalPlayer localAttacker = WorldGuardPlugin.inst().wrapPlayer(playerAttacker);
            Player defender = (Player)event.getEntity();
            if (Entities.isNPC((Entity)defender)) {
                return;
            }
            boolean bl2 = canDamage = query.testBuild(target, associable, RegionProtectionListener.combine(event, Flags.PVP)) && query.queryState(localAttacker.getLocation(), localAttacker, RegionProtectionListener.combine(event, Flags.PVP)) != StateFlag.State.DENY && query.queryState(target, localAttacker, RegionProtectionListener.combine(event, Flags.PVP)) != StateFlag.State.DENY;
            if (!canDamage && Events.fireAndTestCancel(new DisallowedPVPEvent(playerAttacker, defender, event.getOriginalEvent()))) {
                canDamage = true;
            }
            what = "PvP";
        } else if (event.getEntity() instanceof Player) {
            canDamage = event.getRelevantFlags().isEmpty() || query.queryState(target, associable, RegionProtectionListener.combine(event, new StateFlag[0])) != StateFlag.State.DENY;
            what = "damage that";
        } else if (Entities.isNonHostile(event.getEntity())) {
            canDamage = query.testBuild(target, associable, RegionProtectionListener.combine(event, Flags.DAMAGE_ANIMALS));
            what = "harm that";
        } else {
            canDamage = query.testBuild(target, associable, RegionProtectionListener.combine(event, Flags.INTERACT));
            what = "hit that";
        }
        if (!canDamage) {
            this.tellErrorMessage(event, event.getCause(), event.getTarget(), what);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onVehicleExit(VehicleExitEvent event) {
        Player player;
        Vehicle vehicle = event.getVehicle();
        if (!RegionProtectionListener.isRegionSupportEnabled(vehicle.getWorld())) {
            return;
        }
        LivingEntity exited = event.getExited();
        if (vehicle instanceof Tameable && exited instanceof Player && !Entities.isNPC((Entity)(player = (Player)exited))) {
            Location location;
            RegionQuery query;
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if (!this.isWhitelisted(Cause.create(player), vehicle.getWorld(), false) && !(query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery()).testBuild(BukkitAdapter.adapt((Location)(location = vehicle.getLocation())), localPlayer, Flags.RIDE, Flags.INTERACT)) {
                long now = System.currentTimeMillis();
                Long lastTime = WGMetadata.getIfPresent((Metadatable)player, DISEMBARK_MESSAGE_KEY, Long.class);
                if (lastTime == null || now - lastTime >= 500L) {
                    player.sendMessage(String.valueOf(ChatColor.GOLD) + "Don't disembark here!" + String.valueOf(ChatColor.GRAY) + " You can't get back on.");
                    WGMetadata.put((Metadatable)player, DISEMBARK_MESSAGE_KEY, now);
                }
                event.setCancelled(true);
            }
        }
    }

    private static StateFlag[] combine(DelegateEvent event, StateFlag ... flag) {
        List<StateFlag> extra = event.getRelevantFlags();
        StateFlag[] flags = Arrays.copyOf(flag, flag.length + extra.size());
        for (int i = 0; i < extra.size(); ++i) {
            flags[flag.length + i] = extra.get(i);
        }
        return flags;
    }

    private static boolean handleAsInventoryUsage(Event event) {
        return event instanceof PlayerTakeLecternBookEvent;
    }
}

