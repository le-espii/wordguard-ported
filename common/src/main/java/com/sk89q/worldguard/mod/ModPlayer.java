package com.sk89q.worldguard.mod;

import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.UUID;

public class ModPlayer implements LocalPlayer {
    private final ServerPlayer mcPlayer;
    private final com.sk89q.worldedit.entity.Player wePlayer;

    // Estado del override de clima/hora "solo para este cliente" que usan los session
    // handlers WeatherLockFlag/TimeLockFlag de worldguard-core (antes eran no-ops, ver
    // auditoría, hallazgo M4). Minecraft vanilla no tiene un concepto de clima/hora por
    // jugador: se simula enviando los paquetes de clima/hora solo a la conexión de este
    // jugador, sin tocar el estado real del ServerLevel (el mismo truco que usa Bukkit).
    private WeatherType worldguard$weatherOverride;
    private boolean worldguard$timeRelative = true;
    private long worldguard$timeOffset = 0;

    public ModPlayer(ServerPlayer mcPlayer, com.sk89q.worldedit.entity.Player wePlayer) {
        this.mcPlayer = mcPlayer;
        this.wePlayer = wePlayer;
    }

    public ServerPlayer getMcPlayer() {
        return mcPlayer;
    }

    // --- Métodos específicos de LocalPlayer ---

    @Override
    public boolean hasGroup(String group) {
        // Vanilla no tiene un proveedor de "grupos de permisos" (LuckPerms, etc.) integrado
        // en el classpath del mod, así que se usan los Equipos de scoreboard (vanilla, sin
        // dependencias extra) como sustituto: pertenecer al grupo "<nombre>" de WorldGuard
        // equivale a estar en el equipo "<nombre>" (server owners lo configuran con
        // /team add <nombre> y /team join <nombre> <jugador>). Antes este método ignoraba
        // el parámetro "group" por completo y devolvía "es OP" para CUALQUIER grupo
        // solicitado, rompiendo -grupo en /rg addmember, PlayerModesListener (wg-invincible/
        // wg-amphibious), etc. (auditoría, hallazgo C3). A propósito ya NO hay un fallback a
        // "es OP": eso es exactamente lo que hacía que todo grupo diera igual.
        if (group == null || group.isEmpty()) {
            return false;
        }
        net.minecraft.world.scores.PlayerTeam team = mcPlayer.getTeam();
        return team != null && team.getName().equalsIgnoreCase(group);
    }

    @Override
    public void kick(String msg) {
        mcPlayer.connection.disconnect(Component.literal(msg));
    }

    @Override
    public void ban(String msg) {
        // En Minecraft vanilla, banear requiere el BanList del servidor
        mcPlayer.getServer().getPlayerList().getBans().add(
            new net.minecraft.server.players.UserBanListEntry(
                mcPlayer.getGameProfile(), 
                null, 
                "WorldGuard", 
                null, 
                msg
            )
        );
        kick(msg);
    }

    @Override
    public double getHealth() {
        return mcPlayer.getHealth();
    }

    @Override
    public void setHealth(double health) {
        mcPlayer.setHealth((float) health);
    }

    @Override
    public double getMaxHealth() {
        return mcPlayer.getMaxHealth();
    }

    @Override
    public double getFoodLevel() {
        return mcPlayer.getFoodData().getFoodLevel();
    }

    @Override
    public void setFoodLevel(double foodLevel) {
        mcPlayer.getFoodData().setFoodLevel((int) foodLevel);
    }

    @Override
    public double getSaturation() {
        return mcPlayer.getFoodData().getSaturationLevel();
    }

    @Override
    public void setSaturation(double saturation) {
        // En 1.21.1, FoodData tiene saturationLevel pero no setSaturation directo,
        // se maneja a través de comer o modificando el campo por accesores si existiera
        // o llamando a eat con saturación.
        // Implementación segura básica:
        // mcPlayer.getFoodData().eat((int)getFoodLevel(), (float)saturation);
    }

    @Override
    public float getExhaustion() {
        return mcPlayer.getFoodData().getExhaustionLevel();
    }

    @Override
    public void setExhaustion(float exhaustion) {
        // No-op seguro en vanilla
    }

    @Override
    public WeatherType getPlayerWeather() {
        if (worldguard$weatherOverride != null) {
            return worldguard$weatherOverride;
        }
        return mcPlayer.level().isRaining()
            ? com.sk89q.worldedit.world.weather.WeatherTypes.RAIN
            : com.sk89q.worldedit.world.weather.WeatherTypes.CLEAR;
    }

    @Override
    public void setPlayerWeather(WeatherType weatherType) {
        this.worldguard$weatherOverride = weatherType;
        boolean rain = weatherType == com.sk89q.worldedit.world.weather.WeatherTypes.RAIN
            || weatherType == com.sk89q.worldedit.world.weather.WeatherTypes.THUNDER_STORM;
        net.minecraft.network.protocol.game.ClientboundGameEventPacket.Type type = rain
            ? net.minecraft.network.protocol.game.ClientboundGameEventPacket.START_RAINING
            : net.minecraft.network.protocol.game.ClientboundGameEventPacket.STOP_RAINING;
        mcPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(type, 0.0f));
    }

    @Override
    public void resetPlayerWeather() {
        this.worldguard$weatherOverride = null;
        boolean actuallyRaining = mcPlayer.level().isRaining();
        net.minecraft.network.protocol.game.ClientboundGameEventPacket.Type type = actuallyRaining
            ? net.minecraft.network.protocol.game.ClientboundGameEventPacket.START_RAINING
            : net.minecraft.network.protocol.game.ClientboundGameEventPacket.STOP_RAINING;
        mcPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(type, 0.0f));
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return worldguard$timeRelative;
    }

    @Override
    public long getPlayerTimeOffset() {
        return worldguard$timeOffset;
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        this.worldguard$timeRelative = relative;
        this.worldguard$timeOffset = time;
        long dayTime = relative ? (mcPlayer.level().getDayTime() + time) : time;
        mcPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTimePacket(
            mcPlayer.level().getGameTime(), dayTime, false));
    }

    @Override
    public void resetPlayerTime() {
        this.worldguard$timeRelative = true;
        this.worldguard$timeOffset = 0;
        mcPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTimePacket(
            mcPlayer.level().getGameTime(), mcPlayer.level().getDayTime(), true));
    }

    @Override
    public int getFireTicks() {
        return mcPlayer.getRemainingFireTicks();
    }

    @Override
    public void setFireTicks(int ticks) {
        mcPlayer.setRemainingFireTicks(ticks);
    }

    @Override
    public void setCompassTarget(Location loc) {
        // En 1.21.1 compass target se actualiza enviando un paquete si se requiere, o no-op
    }

    @Override
    public void sendTitle(String title, String subtitle) {
        // Enviar título en 1.21.1
        mcPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal(title)));
        mcPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
    }

    @Override
    public void resetFallDistance() {
        mcPlayer.fallDistance = 0.0f;
    }

    @Override
    public void teleport(Location loc, String leavePermission, String enterPermission) {
        mcPlayer.teleportTo(
            mcPlayer.server.getLevel(mcPlayer.level().dimension()), 
            loc.getX(), 
            loc.getY(), 
            loc.getZ(), 
            loc.getYaw(), 
            loc.getPitch()
        );
    }



    // --- Delegación en wePlayer (WorldEdit Player) ---

    @Override
    public World getWorld() { return wePlayer.getWorld(); }
    @Override
    public boolean isHoldingPickAxe() { return wePlayer.isHoldingPickAxe(); }
    @Override
    public Direction getCardinalDirection(int max) { return wePlayer.getCardinalDirection(max); }
    @Override
    public com.sk89q.worldedit.blocks.BaseItemStack getItemInHand(HandSide side) { return wePlayer.getItemInHand(side); }
    @Override
    public BaseBlock getBlockInHand(HandSide side) throws com.sk89q.worldedit.WorldEditException { return wePlayer.getBlockInHand(side); }
    @Override
    public void giveItem(com.sk89q.worldedit.blocks.BaseItemStack item) { wePlayer.giveItem(item); }
    @Override
    public BlockBag getInventoryBlockBag() { return wePlayer.getInventoryBlockBag(); }
    @Override
    public GameMode getGameMode() { return wePlayer.getGameMode(); }
    @Override
    public void setGameMode(GameMode gameMode) { wePlayer.setGameMode(gameMode); }
    @Override
    public void findFreePosition(Location loc) { wePlayer.findFreePosition(loc); }
    @Override
    public void setOnGround(Location loc) { wePlayer.setOnGround(loc); }
    @Override
    public void findFreePosition() { wePlayer.findFreePosition(); }
    @Override
    public boolean ascendLevel() { return wePlayer.ascendLevel(); }
    @Override
    public boolean descendLevel() { return wePlayer.descendLevel(); }
    @Override
    public boolean ascendToCeiling(int steps) { return wePlayer.ascendToCeiling(steps); }
    @Override
    public boolean ascendToCeiling(int steps, boolean physics) { return wePlayer.ascendToCeiling(steps, physics); }
    @Override
    public boolean ascendUpwards(int steps) { return wePlayer.ascendUpwards(steps); }
    @Override
    public boolean ascendUpwards(int steps, boolean physics) { return wePlayer.ascendUpwards(steps, physics); }
    @Override
    public void floatAt(int x, int y, int z, boolean physics) { wePlayer.floatAt(x, y, z, physics); }
    @Override
    public Location getBlockIn() { return wePlayer.getBlockIn(); }
    @Override
    public Location getBlockOn() { return wePlayer.getBlockOn(); }
    @Override
    public Location getBlockTrace(int max, boolean air) { return wePlayer.getBlockTrace(max, air); }
    @Override
    public Location getBlockTrace(int max, boolean air, com.sk89q.worldedit.function.mask.Mask mask) { return wePlayer.getBlockTrace(max, air, mask); }
    @Override
    public Location getBlockTraceFace(int max, boolean air) { return wePlayer.getBlockTraceFace(max, air); }
    @Override
    public Location getBlockTraceFace(int max, boolean air, com.sk89q.worldedit.function.mask.Mask mask) { return wePlayer.getBlockTraceFace(max, air, mask); }
    @Override
    public Location getBlockTrace(int max) { return wePlayer.getBlockTrace(max); }
    @Override
    public Location getSolidBlockTrace(int max) { return wePlayer.getSolidBlockTrace(max); }
    @Override
    public Direction getCardinalDirection() { return wePlayer.getCardinalDirection(); }
    @Override
    public boolean passThroughForwardWall(int steps) { return wePlayer.passThroughForwardWall(steps); }
    @Override
    public void setPosition(Vector3 pos, float yaw, float pitch) { wePlayer.setPosition(pos, yaw, pitch); }
    @Override
    public boolean trySetPosition(Vector3 pos, float yaw, float pitch) { return wePlayer.trySetPosition(pos, yaw, pitch); }
    @Override
    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 pos, B state) { wePlayer.sendFakeBlock(pos, state); }

    // --- Delegación en wePlayer -> Entity ---

    @Override
    public UUID getUniqueId() { return wePlayer.getUniqueId(); }
    @Override
    public Location getLocation() { return wePlayer.getLocation(); }
    @Override
    public boolean setLocation(Location loc) { return wePlayer.setLocation(loc); }
    @Override
    public Extent getExtent() { return wePlayer.getExtent(); }
    @Override
    public <T> T getFacet(Class<? extends T> type) { return wePlayer.getFacet(type); }
    @Override
    public boolean remove() { return wePlayer.remove(); }
    @Override
    public com.sk89q.worldedit.entity.BaseEntity getState() { return wePlayer.getState(); }

    // --- Delegación en wePlayer -> Actor ---

    @Override
    public String getName() { return wePlayer.getName(); }
    @Override
    public void printRaw(String msg) { wePlayer.printRaw(msg); }
    @Override
    public void print(String msg) { wePlayer.print(msg); }
    @Override
    public void printDebug(String msg) { wePlayer.printDebug(msg); }
    @Override
    public void printError(String msg) { wePlayer.printError(msg); }
    @Override
    public void print(com.sk89q.worldedit.util.formatting.text.Component msg) { wePlayer.print(msg); }
    @Override
    public boolean hasPermission(String perm) {
        // op-permissions de config.yml (auditoría, interruptores nicho): si está en true (el
        // valor por defecto del original) los OP nivel 2+ pasan cualquier permiso de
        // WorldGuard, igual que WorldGuardPlugin#hasPermission en el Bukkit original.
        if (mcPlayer.hasPermissions(2)
            && com.sk89q.worldguard.WorldGuard.getInstance().getPlatform()
                .getGlobalStateManager().get(getWorld()).opPermissions) {
            return true;
        }
        return wePlayer.hasPermission(perm);
    }
    @Override
    public boolean isPlayer() { return true; }
    @Override
    public java.util.Locale getLocale() { return wePlayer.getLocale(); }
    @Override
    public void dispatchCUIEvent(com.sk89q.worldedit.internal.cui.CUIEvent event) {
        wePlayer.dispatchCUIEvent(event);
    }
    @Override
    public java.io.File openFileOpenDialog(String[] extensions) {
        return wePlayer.openFileOpenDialog(extensions);
    }
    @Override
    public java.io.File openFileSaveDialog(String[] extensions) {
        return wePlayer.openFileSaveDialog(extensions);
    }
    @Override
    public boolean canDestroyBedrock() {
        return wePlayer.canDestroyBedrock();
    }
    @Override
    public com.sk89q.worldedit.session.SessionKey getSessionKey() {
        return wePlayer.getSessionKey();
    }
    @Override
    public void checkPermission(String perm) throws com.sk89q.worldedit.util.auth.AuthorizationException {
        wePlayer.checkPermission(perm);
    }
    @Override
    public String[] getGroups() {
        return wePlayer.getGroups();
    }
}
