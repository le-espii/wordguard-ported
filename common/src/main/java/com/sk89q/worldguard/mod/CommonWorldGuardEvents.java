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

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommonWorldGuardEvents {

    // Límite de frecuencia de mensajes de denegación por jugador (auditoría, hallazgo M1):
    // el original limita a un mensaje cada 500ms por jugador (RegionProtectionListener,
    // vía WGMetadata) para no inundar el chat cuando se repite la misma acción bloqueada
    // muchas veces por segundo (p. ej. mantener presionado el botón de romper un bloque).
    private static final Map<UUID, Long> worldguard$lastDenyMessage = new ConcurrentHashMap<>();
    private static final long DENY_MESSAGE_COOLDOWN_MS = 500;

    private static RegionQuery getQuery() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
    }

    /**
     * Interruptores "duros" de config.yml (independientes de las regiones): con C2 arreglado,
     * ya se cargan de verdad, pero antes de estos usos nadie los consultaba. Se aplican con
     * lógica OR frente al flag de región correspondiente (si cualquiera de los dos bloquea,
     * se bloquea), igual que WorldGuardBlockListener/WorldGuardEntityListener en el original.
     */
    private static com.sk89q.worldguard.config.WorldConfiguration getWorldConfig(Level level) {
        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        return WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
    }

    /**
     * Envía un mensaje de denegación al jugador, respetando el flag de región
     * Flags.DENY_MESSAGE (mensaje personalizable con el macro %what%) y el límite de
     * frecuencia de 500ms, igual que RegionProtectionListener#tellErrorMessage en el
     * original. Antes el port mandaba un mensaje fijo en español en cada intento, sin
     * límite ni forma de personalizarlo por región (hallazgo M1).
     *
     * @param whatKey clave de localización (sufijo de "what.", ver lang/*.json) de la frase
     *                que describe la acción bloqueada, p. ej. "build" para "what.build".
     *                Se usa tanto para armar el mensaje por defecto como para el macro
     *                %what% dentro de un DENY_MESSAGE personalizado.
     */
    public static void tellDenyMessage(ServerPlayer player, com.sk89q.worldedit.util.Location loc, LocalPlayer localPlayer, String whatKey) {
        if (player == null) return;

        long now = System.currentTimeMillis();
        UUID id = player.getUUID();
        Long last = worldguard$lastDenyMessage.get(id);
        if (last != null && now - last < DENY_MESSAGE_COOLDOWN_MS) {
            return;
        }
        worldguard$lastDenyMessage.put(id, now);

        String what = WorldGuardLang.get(player, "what." + whatKey);
        String custom = getQuery().queryValue(loc, localPlayer, Flags.DENY_MESSAGE);
        String msg = custom != null ? custom.replace("%what%", what) : WorldGuardLang.get(player, "deny.default", what);
        player.sendSystemMessage(Component.literal(msg));
    }

    private static com.sk89q.worldedit.util.Location weLoc(Level level, BlockPos pos) {
        World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        return new com.sk89q.worldedit.util.Location(world, com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3());
    }

    /**
     * Aplica Flags.FIRE_SPREAD y el interruptor duro fire.disable-all-fire-spread de
     * config.yml (auditoría, hallazgo H1) para FireBlock#checkBurnOut(...). Vive aquí en vez
     * de en un mixin de common porque NeoForge parchea ese método añadiéndole un Direction
     * final que vanilla/Fabric no tiene (bug descubierto en pruebas: un solo FireBlockMixin
     * compartido no puede tener dos firmas de handler distintas para el mismo método —
     * Mixin valida el descriptor exacto y aborta el arranque en NeoForge sin importar
     * require = 0). Por eso hay un FireBlockMixin fino y específico por plataforma
     * (fabric/src y neoforge/src, cada uno con su propio worldguard-<loader>.mixins.json)
     * que solo llama a este helper común.
     *
     * @return false si el fuego debe cancelarse (no quemar/incendiar el bloque vecino).
     */
    public static boolean checkFireBurnOut(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (level.isClientSide()) return true;

        com.sk89q.worldguard.config.WorldConfiguration wcfg = getWorldConfig(level);
        if (wcfg.disableFireSpread) {
            return false;
        }

        return getQuery().queryState(weLoc(level, pos), (LocalPlayer) null, Flags.FIRE_SPREAD) != StateFlag.State.DENY;
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

            // Interruptores duros de config.yml para tipos de daño concretos (independientes
            // de las regiones, ver getWorldConfig): antes de cablear esto, config.yml no
            // tenía ningún efecto sobre disable-fall-damage/lava-damage/fire-damage/
            // lightning-damage/void-damage aunque ya se cargaban desde el archivo.
            com.sk89q.worldguard.config.WorldConfiguration wcfg = getWorldConfig(level);
            if (source.is(net.minecraft.world.damagesource.DamageTypes.FALL) && wcfg.disableFallDamage) {
                return false;
            }
            if (source.is(net.minecraft.world.damagesource.DamageTypes.LAVA) && wcfg.disableLavaDamage) {
                return false;
            }
            if ((source.is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE) || source.is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE))
                    && wcfg.disableFireDamage) {
                return false;
            }
            if (source.is(net.minecraft.world.damagesource.DamageTypes.LIGHTNING_BOLT) && wcfg.disableLightningDamage) {
                return false;
            }
            if (source.is(net.minecraft.world.damagesource.DamageTypes.DROWN) && wcfg.disableDrowningDamage) {
                return false;
            }
            if (source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL) && wcfg.disableSuffocationDamage) {
                return false;
            }
            if (source.is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD) && wcfg.disableVoidDamage) {
                return false;
            }

            // Daño de caída
            if (source.is(net.minecraft.world.damagesource.DamageTypes.FALL)) {
                if (query.queryState(targetLoc, localDefender, Flags.FALL_DAMAGE) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    return false;
                }
            }

            // PVP: a diferencia de los demás chequeos, el bypass de región NO exime el flag
            // PVP en el WorldGuard original (RegionProtectionListener#isWhitelisted recibe un
            // parámetro "pvp" que ignora el bypass para este caso concreto), así que aquí no
            // se consulta hasBypass.
            if (localAttacker != null) {
                boolean targetDeny = query.queryState(targetLoc, localAttacker, Flags.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
                boolean attackerDeny = query.queryState(localAttacker.getLocation(), localAttacker, Flags.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
                if (targetDeny || attackerDeny) {
                    tellDenyMessage(playerAttacker, targetLoc, localAttacker, "pvp");
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
                    if (wcfg.disableMobDamage
                        || query.queryState(targetLoc, localDefender, Flags.MOB_DAMAGE) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                        return false;
                    }
                }
            }
        }

        // 2. Ataque de jugadores a entidades protegidas (chequeos de tipo "build permission":
        // aquí el bypass de región sí exime, igual que en el original).
        if (localAttacker != null) {
            if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localAttacker, localAttacker.getWorld())) {
                return true;
            }

            // Vehículos
            if (target instanceof net.minecraft.world.entity.vehicle.Minecart || target instanceof net.minecraft.world.entity.vehicle.Boat) {
                if (query.queryState(targetLoc, localAttacker, Flags.DESTROY_VEHICLE) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    tellDenyMessage(playerAttacker, targetLoc, localAttacker, "vehicle_destroy");
                    return false;
                }
            }
            // Cuadros e Item Frames
            else if (target instanceof Painting) {
                if (query.queryState(targetLoc, localAttacker, Flags.ENTITY_PAINTING_DESTROY) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    tellDenyMessage(playerAttacker, targetLoc, localAttacker, "painting_destroy");
                    return false;
                }
            }
            else if (target instanceof ItemFrame) {
                if (query.queryState(targetLoc, localAttacker, Flags.ENTITY_ITEM_FRAME_DESTROY) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    tellDenyMessage(playerAttacker, targetLoc, localAttacker, "itemframe_destroy");
                    return false;
                }
            }
            // Animales
            else if (target instanceof Animal || target instanceof net.minecraft.world.entity.animal.AbstractGolem || target instanceof net.minecraft.world.entity.npc.Npc) {
                if (query.queryState(targetLoc, localAttacker, Flags.DAMAGE_ANIMALS) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                    tellDenyMessage(playerAttacker, targetLoc, localAttacker, "damage_animals");
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

        boolean isRideable = target instanceof net.minecraft.world.entity.vehicle.Minecart
                || target instanceof net.minecraft.world.entity.vehicle.Boat
                || target instanceof net.minecraft.world.entity.Saddleable;

        if (isRideable) {
            boolean canRide = query.queryState(targetLoc, localPlayer, Flags.RIDE) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY
                    && query.queryState(targetLoc, localPlayer, Flags.INTERACT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!canRide) {
                tellDenyMessage(serverPlayer, targetLoc, localPlayer, "ride");
                return InteractionResult.FAIL;
            }
        } else if (target instanceof net.minecraft.world.entity.decoration.ItemFrame frame && !frame.getItem().isEmpty()) {
            // Rotar el ítem que ya contiene un marco de ítem usa un flag específico
            // (Flags.ITEM_FRAME_ROTATE), no solo INTERACT genérico (hallazgo M3).
            boolean canRotate = query.queryState(targetLoc, localPlayer, Flags.INTERACT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY
                    && query.queryState(targetLoc, localPlayer, Flags.ITEM_FRAME_ROTATE) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!canRotate) {
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
    public static boolean canDropItem(Player player, net.minecraft.world.item.Item item) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        Level level = player.level();
        if (level.isClientSide()) return true;

        ServerPlayer serverPlayer = (ServerPlayer) player;
        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(serverPlayer);
        BlockPos pos = player.blockPosition();

        // La blacklist es un sistema independiente de las regiones (igual que en el
        // original, BlacklistListener no consulta el bypass de región), así que se chequea
        // sin importar si el jugador tiene bypass (hallazgo H3).
        if (!checkDropBlacklist(serverPlayer, level, pos, item)) {
            return false;
        }

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(level, pos);

        boolean canDrop = query.queryState(loc, localPlayer, Flags.ITEM_DROP) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        if (!canDrop) {
            tellDenyMessage(serverPlayer, loc, localPlayer, "item_drop");
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
        BlockPos pos = itemEntity.blockPosition();

        if (!checkAcquireBlacklist(serverPlayer, level, pos, itemEntity.getItem().getItem())) {
            return false;
        }

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(level, pos);

        return query.queryState(loc, localPlayer, Flags.ITEM_PICKUP) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- SPAWNEO DE MOBS Y RAYOS ---
    public static boolean canSpawnEntity(Entity entity, Level level) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (level.isClientSide()) return true;

        RegionQuery query = getQuery();
        com.sk89q.worldedit.util.Location loc = weLoc(level, entity.blockPosition());
        com.sk89q.worldguard.config.WorldConfiguration wcfg = getWorldConfig(level);

        if (entity instanceof net.minecraft.world.entity.ExperienceOrb) {
            return !wcfg.disableExpDrops;
        }

        if (entity instanceof LightningBolt) {
            return query.queryState(loc, null, Flags.LIGHTNING) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        if (entity instanceof Mob) {
            boolean isTamed = entity instanceof net.minecraft.world.entity.OwnableEntity ownable && ownable.getOwnerUUID() != null;
            if (isTamed && wcfg.allowTamedSpawns) {
                return true;
            }

            // mobs.block-above-ground-slimes: sin información de "razón de spawn" disponible
            // en este punto (a diferencia de Bukkit), se aproxima a "cualquier slime que
            // aparezca sobre Y=60", que es el caso que este interruptor busca evitar.
            if (wcfg.blockGroundSlimes && entity instanceof net.minecraft.world.entity.monster.Slime
                    && entity.blockPosition().getY() >= 60) {
                return false;
            }

            // mobs.block-creature-spawn: lista de tipos bloqueados sin importar la región.
            if (!wcfg.blockCreatureSpawn.isEmpty()) {
                ResourceLocation entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
                if (entityKey != null) {
                    com.sk89q.worldedit.world.entity.EntityType weType = com.sk89q.worldedit.world.entity.EntityTypes.get(entityKey.toString());
                    if (weType != null && wcfg.blockCreatureSpawn.contains(weType)) {
                        return false;
                    }
                }
            }

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
        if (getWorldConfig(level).disableIceFormation) return false;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.ICE_FORM) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    public static boolean canSnow(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (getWorldConfig(level).disableSnowFormation) return false;
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
        com.sk89q.worldguard.config.WorldConfiguration wcfg = getWorldConfig(level);

        if (block instanceof net.minecraft.world.level.block.LeavesBlock) {
            return !wcfg.disableLeafDecay && query.queryState(loc, null, Flags.LEAF_DECAY) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.IceBlock) {
            return !wcfg.disableIceMelting && query.queryState(loc, null, Flags.ICE_MELT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.SnowLayerBlock) {
            return !wcfg.disableSnowMelting && query.queryState(loc, null, Flags.SNOW_MELT) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.FarmBlock) {
            return !wcfg.disableSoilDehydration && query.queryState(loc, null, Flags.SOIL_DRY) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.GrassBlock) {
            return !wcfg.disableGrassGrowth && query.queryState(loc, null, Flags.GRASS_SPREAD) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }
        if (block instanceof net.minecraft.world.level.block.MyceliumBlock) {
            return !wcfg.disableMyceliumSpread && query.queryState(loc, null, Flags.MYCELIUM_SPREAD) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        // Cultivos, brotes, tallos, cocoa, bayas dulces, flor antorcha, nether wart.
        if (block instanceof net.minecraft.world.level.block.CropBlock
                || block instanceof net.minecraft.world.level.block.StemBlock
                || block instanceof net.minecraft.world.level.block.SaplingBlock
                || block instanceof net.minecraft.world.level.block.NetherWartBlock
                || block instanceof net.minecraft.world.level.block.SweetBerryBushBlock
                || block instanceof net.minecraft.world.level.block.CocoaBlock
                || block instanceof net.minecraft.world.level.block.TorchflowerCropBlock) {
            return !wcfg.disableCropGrowth && query.queryState(loc, null, Flags.CROP_GROWTH) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        // Enredaderas trepando (Flags.VINE_GROWTH) — sin gancho antes de la auditoría (H11).
        if (block instanceof net.minecraft.world.level.block.VineBlock) {
            return !wcfg.disableVineGrowth && query.queryState(loc, null, Flags.VINE_GROWTH) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        // Crecimiento de amatista y goteo de dripstone (Flags.ROCK_GROWTH) — ídem.
        if (block instanceof net.minecraft.world.level.block.BuddingAmethystBlock
                || block instanceof net.minecraft.world.level.block.PointedDripstoneBlock) {
            return !wcfg.disableRockGrowth && query.queryState(loc, null, Flags.ROCK_GROWTH) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        // Propagación de hongos pequeños (Flags.MUSHROOMS) — ídem.
        if (block instanceof net.minecraft.world.level.block.MushroomBlock) {
            return !wcfg.disableMushroomSpread && query.queryState(loc, null, Flags.MUSHROOMS) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        // Oxidación de cobre sin encerar (Flags.COPPER_FADE) — ídem.
        if (block instanceof net.minecraft.world.level.block.WeatheringCopper) {
            return !wcfg.disableCopperBlockFade && query.queryState(loc, null, Flags.COPPER_FADE) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
        }

        return true;
    }

    // --- PISOTEO DE CULTIVOS ---
    public static boolean canTrampleCrops(Entity trampler, Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        com.sk89q.worldguard.config.WorldConfiguration wcfg = getWorldConfig(level);
        boolean disabledByToggle = trampler instanceof Player ? wcfg.disablePlayerCropTrampling : wcfg.disableCreatureCropTrampling;
        if (disabledByToggle) return false;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.TRAMPLE_BLOCKS) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- ENDERMAN GRIEFING ---
    public static boolean canEndermanGrief(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (getWorldConfig(level).disableEndermanGriefing) return false;
        RegionQuery query = getQuery();
        return query.queryState(weLoc(level, pos), null, Flags.ENDER_BUILD) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
    }

    // --- RASTRO DE NIEVE DE MUÑECO DE NIEVE ---
    public static boolean canSnowmanTrail(Level level, BlockPos pos) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (getWorldConfig(level).disableSnowmanTrails) return false;
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
                tellDenyMessage(player, loc, localPlayer, "command");
                return false;
            }
        }
        return true;
    }

    // --- LISTA NEGRA DE MATERIALES (blacklist.txt) ---
    // Antes de la auditoría (hallazgo H3), el archivo se leía (ModWorldConfiguration) pero
    // ningún Mixin llamaba nunca a Blacklist#check(...): la funcionalidad de blacklist.txt
    // era, en la práctica, inexistente. El propio Blacklist se encarga de los mensajes/logs/
    // acciones configuradas en cada entrada (Action), así que aquí solo hace falta construir
    // el evento correcto y respetar su resultado.

    @FunctionalInterface
    private interface BlacklistEventFactory {
        com.sk89q.worldguard.blacklist.event.BlacklistEvent create(
            LocalPlayer player, com.sk89q.worldedit.math.BlockVector3 pos, com.sk89q.worldguard.blacklist.target.Target target);
    }

    private static com.sk89q.worldguard.blacklist.Blacklist getBlacklist(com.sk89q.worldedit.world.World world) {
        return WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world).getBlacklist();
    }

    private static com.sk89q.worldedit.world.block.BlockType weBlockType(net.minecraft.world.level.block.Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        return key != null ? com.sk89q.worldedit.world.block.BlockTypes.get(key.toString()) : null;
    }

    private static com.sk89q.worldedit.world.item.ItemType weItemType(net.minecraft.world.item.Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return key != null ? com.sk89q.worldedit.world.item.ItemTypes.get(key.toString()) : null;
    }

    private static boolean checkBlockBlacklist(ServerPlayer player, Level level, BlockPos pos,
                                                net.minecraft.world.level.block.Block block, BlacklistEventFactory factory) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (level.isClientSide()) return true;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        com.sk89q.worldguard.blacklist.Blacklist blacklist = getBlacklist(world);
        if (blacklist == null) return true;

        com.sk89q.worldedit.world.block.BlockType type = weBlockType(block);
        if (type == null) return true;

        LocalPlayer localPlayer = player != null ? WorldGuardModCommon.getAdapter().adaptPlayer(player) : null;
        com.sk89q.worldedit.math.BlockVector3 vec = com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ());
        com.sk89q.worldguard.blacklist.target.Target target = new com.sk89q.worldguard.blacklist.target.BlockTarget(type);
        return blacklist.check(factory.create(localPlayer, vec, target), false, false);
    }

    private static boolean checkItemBlacklist(ServerPlayer player, Level level, BlockPos pos,
                                               net.minecraft.world.item.Item item, BlacklistEventFactory factory) {
        if (WorldGuardModCommon.getAdapter() == null) return true;
        if (level.isClientSide()) return true;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        com.sk89q.worldguard.blacklist.Blacklist blacklist = getBlacklist(world);
        if (blacklist == null) return true;

        com.sk89q.worldedit.world.item.ItemType type = weItemType(item);
        if (type == null) return true;

        LocalPlayer localPlayer = player != null ? WorldGuardModCommon.getAdapter().adaptPlayer(player) : null;
        com.sk89q.worldedit.math.BlockVector3 vec = com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ());
        com.sk89q.worldguard.blacklist.target.Target target = new com.sk89q.worldguard.blacklist.target.ItemTarget(type);
        return blacklist.check(factory.create(localPlayer, vec, target), false, false);
    }

    public static boolean checkBreakBlacklist(ServerPlayer player, Level level, BlockPos pos) {
        return checkBlockBlacklist(player, level, pos, level.getBlockState(pos).getBlock(),
            com.sk89q.worldguard.blacklist.event.BlockBreakBlacklistEvent::new);
    }

    public static boolean checkPlaceBlacklist(ServerPlayer player, Level level, BlockPos pos, net.minecraft.world.level.block.Block block) {
        return checkBlockBlacklist(player, level, pos, block,
            com.sk89q.worldguard.blacklist.event.BlockPlaceBlacklistEvent::new);
    }

    public static boolean checkInteractBlacklist(ServerPlayer player, Level level, BlockPos pos) {
        return checkBlockBlacklist(player, level, pos, level.getBlockState(pos).getBlock(),
            com.sk89q.worldguard.blacklist.event.BlockInteractBlacklistEvent::new);
    }

    public static boolean checkDropBlacklist(ServerPlayer player, Level level, BlockPos pos, net.minecraft.world.item.Item item) {
        return checkItemBlacklist(player, level, pos, item,
            com.sk89q.worldguard.blacklist.event.ItemDropBlacklistEvent::new);
    }

    public static boolean checkAcquireBlacklist(ServerPlayer player, Level level, BlockPos pos, net.minecraft.world.item.Item item) {
        return checkItemBlacklist(player, level, pos, item,
            com.sk89q.worldguard.blacklist.event.ItemAcquireBlacklistEvent::new);
    }
}
