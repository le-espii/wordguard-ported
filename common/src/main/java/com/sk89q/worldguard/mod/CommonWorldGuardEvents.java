package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.util.command.CommandFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class CommonWorldGuardEvents {

    private static RegionQuery getQuery() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
    }

    private static com.sk89q.worldedit.util.Location weLoc(Level level, BlockPos pos) {
        World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        return new com.sk89q.worldedit.util.Location(world, com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3());
    }

    private static com.sk89q.worldedit.util.Location weLoc(Level level, double x, double y, double z) {
        World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        return new com.sk89q.worldedit.util.Location(world, com.sk89q.worldedit.math.BlockVector3.at(x, y, z).toVector3());
    }

    // --- DAÑO Y COMBATE ---
    public static boolean canDamage(Entity target, DamageSource source, float amount) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        Level level = target.level();
        if (level.isClientSide()) return true;

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location targetLoc = weLoc(level, target.blockPosition());

        Entity attacker = source.getEntity();
        ServerPlayer playerAttacker = null;
        LocalPlayer localAttacker = null;

        if (attacker instanceof ServerPlayer) {
            playerAttacker = (ServerPlayer) attacker;
            localAttacker = WorldGuardModCommon.getAdapter().adaptPlayer(playerAttacker);
        } else if (source.getDirectEntity() instanceof ServerPlayer) {
            playerAttacker = (ServerPlayer) source.getDirectEntity();
            localAttacker = WorldGuardModCommon.getAdapter().adaptPlayer(playerAttacker);
        }

        // 1. Invencibilidad del defensor
        if (target instanceof ServerPlayer) {
            ServerPlayer defender = (ServerPlayer) target;
            LocalPlayer localDefender = WorldGuardModCommon.getAdapter().adaptPlayer(defender);
            Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(localDefender);
            if (session.isInvincible(localDefender)) {
                return false;
            }

            // Daño de caída
            if (source.is(net.minecraft.world.damagesource.DamageTypes.FALL)) {
                if (query.queryState(targetLoc, localDefender, Flags.FALL_DAMAGE) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    return false;
                }
            }

            // PVP
            if (localAttacker != null) {
                if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localAttacker, localAttacker.getWorld())) {
                    return true;
                }
                boolean targetDeny = query.queryState(targetLoc, localAttacker, Flags.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
                boolean attackerDeny = query.queryState(localAttacker.getLocation(), localAttacker, Flags.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
                if (targetDeny || attackerDeny) {
                    playerAttacker.sendSystemMessage(Component.literal("§cEl PVP está desactivado aquí."));
                    return false;
                }
            }

            // Daño de mobs
            if (attacker instanceof Mob) {
                boolean isTamed = false;
                if (attacker instanceof OwnableEntity) {
                    isTamed = ((OwnableEntity) attacker).getOwnerUUID() != null;
                }
                if (!isTamed) {
                    if (query.queryState(targetLoc, localDefender, Flags.MOB_DAMAGE) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                        return false;
                    }
                }
            }
        }

        // 2. Ataque de jugadores a entidades protegidas
        if (localAttacker != null) {
            if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localAttacker, localAttacker.getWorld())) {
                return true;
            }

            // Vehículos
            if (target instanceof net.minecraft.world.entity.vehicle.Minecart || target instanceof net.minecraft.world.entity.vehicle.Boat) {
                if (query.queryState(targetLoc, localAttacker, Flags.DESTROY_VEHICLE) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    playerAttacker.sendSystemMessage(Component.literal("§cNo tienes permiso para destruir vehículos aquí."));
                    return false;
                }
            }
            // Cuadros e Item Frames
            else if (target instanceof Painting) {
                if (query.queryState(targetLoc, localAttacker, Flags.ENTITY_PAINTING_DESTROY) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    playerAttacker.sendSystemMessage(Component.literal("§cNo tienes permiso para destruir cuadros aquí."));
                    return false;
                }
            }
            else if (target instanceof ItemFrame) {
                if (query.queryState(targetLoc, localAttacker, Flags.ENTITY_ITEM_FRAME_DESTROY) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    playerAttacker.sendSystemMessage(Component.literal("§cNo tienes permiso para destruir marcos de ítems aquí."));
                    return false;
                }
            }
            // Animales
            else if (target instanceof Animal || target instanceof net.minecraft.world.entity.animal.AbstractGolem || target instanceof net.minecraft.world.entity.npc.Npc) {
                if (query.queryState(targetLoc, localAttacker, Flags.DAMAGE_ANIMALS) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    playerAttacker.sendSystemMessage(Component.literal("§cNo tienes permiso para dañar animales aquí."));
                    return false;
                }
            }
            // Bloquear ataques generales
            else {
                if (query.queryState(targetLoc, localAttacker, Flags.INTERACT) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    return false;
                }
            }
        }

        return true;
    }

    // --- INTERACCIONES CON ENTIDADES ---
    public static InteractionResult onInteractEntity(Player player, Entity target, InteractionHand hand) {
        if (WorldGuardModCommon.getAdapter() == null) return InteractionResult.PASS;
        Level level = player.level();
        if (level.isClientSide()) return InteractionResult.PASS;

        ServerPlayer serverPlayer = (ServerPlayer) player;
        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(serverPlayer);

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return InteractionResult.PASS;
        }

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location targetLoc = weLoc(level, target.blockPosition());

        String className = target.getClass().getName();
        boolean isRideable = target instanceof net.minecraft.world.entity.vehicle.Minecart 
                || target instanceof net.minecraft.world.entity.vehicle.Boat 
                || target instanceof net.minecraft.world.entity.Saddleable
                || className.contains("Horse") 
                || className.contains("Camel") 
                || className.contains("Strider");

        if (isRideable) {
            boolean canRide = query.queryState(targetLoc, localPlayer, Flags.RIDE) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY
                    && query.queryState(targetLoc, localPlayer, Flags.INTERACT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!canRide) {
                serverPlayer.sendSystemMessage(Component.literal("§cNo tienes permiso para montar vehículos/entidades aquí."));
                return InteractionResult.FAIL;
            }
        } else {
            boolean canInteract = query.queryState(targetLoc, localPlayer, Flags.INTERACT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!canInteract) {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }

    // --- RECOGIDA Y LANZAMIENTO DE ÍTEMS ---
    public static boolean canDropItem(Player player) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        Level level = player.level();
        if (level.isClientSide()) return true;

        ServerPlayer serverPlayer = (ServerPlayer) player;
        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(serverPlayer);

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(level, player.blockPosition());

        boolean canDrop = query.queryState(loc, localPlayer, Flags.ITEM_DROP) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        if (!canDrop) {
            serverPlayer.sendSystemMessage(Component.literal("§cNo tienes permiso para tirar ítems aquí."));
            return false;
        }
        return true;
    }

    public static boolean canPickupItem(ItemEntity itemEntity, Player player) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        Level level = itemEntity.level();
        if (level.isClientSide()) return true;

        ServerPlayer serverPlayer = (ServerPlayer) player;
        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(serverPlayer);

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(level, itemEntity.blockPosition());

        return query.queryState(loc, localPlayer, Flags.ITEM_PICKUP) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- SPAWNEO DE MOBS Y RAYOS ---
    public static boolean canSpawnEntity(Entity entity, Level level) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (level.isClientSide()) return true;

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(level, entity.blockPosition());

        if (entity instanceof LightningBolt) {
            return query.queryState(loc, null, Flags.LIGHTNING) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        if (entity instanceof Mob) {
            // General mob-spawning check
            boolean canSpawn = query.queryState(loc, null, Flags.MOB_SPAWNING) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!canSpawn) {
                return false;
            }

            // Specific deny-spawn check
            ApplicableRegionSet set = query.getApplicableRegions(loc);
            Set<com.sk89q.worldedit.world.entity.EntityType> deniedTypes = 
                    (Set<com.sk89q.worldedit.world.entity.EntityType>) set.queryValue(null, Flags.DENY_SPAWN);
            if (deniedTypes != null && !deniedTypes.isEmpty()) {
                ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
                if (key != null) {
                    com.sk89q.worldedit.world.entity.EntityType weEntityType = 
                            com.sk89q.worldedit.world.entity.EntityTypes.get(key.toString());
                    if (weEntityType != null && deniedTypes.contains(weEntityType)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // --- INTERCEPCIÓN DE CLIMA Y FLUIDOS DE BIOMA ---
    public static boolean canFreeze(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.ICE_FORM) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    public static boolean canSnow(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.SNOW_FALL) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- DECENAS DE CULTIVOS Y BLOQUES (RANDOM TICKS) ---
    public static boolean canRandomTick(BlockState state, Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (level.isClientSide()) return true;

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(level, pos);
        net.minecraft.world.level.block.Block block = state.getBlock();

        if (block instanceof net.minecraft.world.level.block.LeavesBlock) {
            return query.queryState(loc, null, Flags.LEAF_DECAY) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.IceBlock) {
            return query.queryState(loc, null, Flags.ICE_MELT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.SnowLayerBlock) {
            return query.queryState(loc, null, Flags.SNOW_MELT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.FarmBlock) {
            return query.queryState(loc, null, Flags.SOIL_DRY) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.GrassBlock) {
            return query.queryState(loc, null, Flags.GRASS_SPREAD) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.MyceliumBlock) {
            return query.queryState(loc, null, Flags.MYCELIUM_SPREAD) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        
        // Cultivos, brotes, etc.
        String className = block.getClass().getName();
        if (block instanceof net.minecraft.world.level.block.CropBlock 
                || block instanceof net.minecraft.world.level.block.SaplingBlock 
                || className.contains("Crop") 
                || className.contains("Stem") 
                || className.contains("Sapling") 
                || className.contains("SweetBerry") 
                || className.contains("Cocoa")) {
            return query.queryState(loc, null, Flags.CROP_GROWTH) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        return true;
    }

    // --- PISOTEO DE CULTIVOS ---
    public static boolean canTrampleCrops(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.TRAMPLE_BLOCKS) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- ENDERMAN GRIEFING ---
    public static boolean canEndermanGrief(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.ENDER_BUILD) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- RASTRO DE NIEVE DE MUÑECO DE NIEVE ---
    public static boolean canSnowmanTrail(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.SNOWMAN_TRAILS) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- RAVAGER GRIEFING ---
    public static boolean canRavagerRavage(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.RAVAGER_RAVAGE) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- FILTRADO DE COMANDOS ---
    public static boolean canExecuteCommand(ServerPlayer player, String command) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(player);

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(player.level(), player.blockPosition());
        ApplicableRegionSet set = query.getApplicableRegions(loc);

        Set<String> allowedCommands = (Set<String>) set.queryValue(localPlayer, Flags.ALLOWED_CMDS);
        Set<String> blockedCommands = (Set<String>) set.queryValue(localPlayer, Flags.BLOCKED_CMDS);

        if (allowedCommands != null || blockedCommands != null) {
            CommandFilter filter = new CommandFilter(allowedCommands, blockedCommands);
            if (!filter.apply(command)) {
                player.sendSystemMessage(Component.literal("§cNo tienes permiso para ejecutar ese comando en esta región."));
                return false;
            }
        }
        return true;
    }
}
