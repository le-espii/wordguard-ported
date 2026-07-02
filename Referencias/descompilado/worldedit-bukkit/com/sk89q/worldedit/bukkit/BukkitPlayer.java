/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayerBlockBag;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.TextAdapter;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.enginehub.linbus.tree.LinCompoundTag;

public class BukkitPlayer
extends AbstractPlayerActor {
    private final Player player;
    private final WorldEditPlugin plugin;

    public BukkitPlayer(Player player) {
        this(WorldEditPlugin.getInstance(), player);
    }

    public BukkitPlayer(WorldEditPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public BaseItemStack getItemInHand(HandSide handSide) {
        ItemStack itemStack = handSide == HandSide.MAIN_HAND ? this.player.getInventory().getItemInMainHand() : this.player.getInventory().getItemInOffHand();
        return BukkitAdapter.adapt(itemStack);
    }

    @Override
    public BaseBlock getBlockInHand(HandSide handSide) throws WorldEditException {
        ItemStack itemStack = handSide == HandSide.MAIN_HAND ? this.player.getInventory().getItemInMainHand() : this.player.getInventory().getItemInOffHand();
        return BukkitAdapter.asBlockState(itemStack).toBaseBlock();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public String getDisplayName() {
        return this.player.getDisplayName();
    }

    @Override
    public void giveItem(BaseItemStack itemStack) {
        this.player.getInventory().addItem(new ItemStack[]{BukkitAdapter.adapt(itemStack)});
    }

    @Override
    @Deprecated
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            this.player.sendMessage(part);
        }
    }

    @Override
    @Deprecated
    public void print(String msg) {
        for (String part : msg.split("\n")) {
            this.player.sendMessage("\u00a7d" + part);
        }
    }

    @Override
    @Deprecated
    public void printDebug(String msg) {
        for (String part : msg.split("\n")) {
            this.player.sendMessage("\u00a77" + part);
        }
    }

    @Override
    @Deprecated
    public void printError(String msg) {
        for (String part : msg.split("\n")) {
            this.player.sendMessage("\u00a7c" + part);
        }
    }

    @Override
    public void print(Component component) {
        TextAdapter.sendMessage((CommandSender)this.player, WorldEditText.format(component, this.getLocale()));
    }

    @Override
    public boolean trySetPosition(Vector3 pos, float pitch, float yaw) {
        return this.player.teleport(new Location(this.player.getWorld(), pos.x(), pos.y(), pos.z(), yaw, pitch));
    }

    @Override
    public String[] getGroups() {
        return this.plugin.getPermissionsResolver().getGroups((OfflinePlayer)this.player);
    }

    @Override
    public BlockBag getInventoryBlockBag() {
        return new BukkitPlayerBlockBag(this.player);
    }

    @Override
    public GameMode getGameMode() {
        return GameModes.get(this.player.getGameMode().name().toLowerCase(Locale.ROOT));
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.player.setGameMode(org.bukkit.GameMode.valueOf((String)gameMode.id().toUpperCase(Locale.ROOT)));
    }

    @Override
    public boolean hasPermission(String perm) {
        return !this.plugin.getLocalConfiguration().noOpPermissions && this.player.isOp() || this.plugin.getPermissionsResolver().hasPermission(this.player.getWorld().getName(), (OfflinePlayer)this.player, perm);
    }

    @Override
    public World getWorld() {
        return BukkitAdapter.adapt(this.player.getWorld());
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
        String[] params = event.getParameters();
        Object send = event.getTypeId();
        if (params.length > 0) {
            send = (String)send + "|" + StringUtil.joinString(params, "|");
        }
        this.player.sendPluginMessage((Plugin)this.plugin, "worldedit:cui", ((String)send).getBytes(StandardCharsets.UTF_8));
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isAllowedToFly() {
        return this.player.getAllowFlight();
    }

    @Override
    public void setFlying(boolean flying) {
        this.player.setFlying(flying);
    }

    @Override
    public BaseEntity getState() {
        throw new UnsupportedOperationException("Cannot create a state from this object");
    }

    @Override
    public com.sk89q.worldedit.util.Location getLocation() {
        Location nativeLocation = this.player.getLocation();
        Vector3 position = BukkitAdapter.asVector(nativeLocation);
        return new com.sk89q.worldedit.util.Location((Extent)this.getWorld(), position, nativeLocation.getYaw(), nativeLocation.getPitch());
    }

    @Override
    public boolean setLocation(com.sk89q.worldedit.util.Location location) {
        return this.player.teleport(BukkitAdapter.adapt(location));
    }

    @Override
    public Locale getLocale() {
        return TextUtils.getLocaleByMinecraftTag(this.player.getLocale());
    }

    @Override
    public void sendAnnouncements() {
        if (!WorldEditPlugin.getInstance().getLifecycledBukkitImplAdapter().isValid()) {
            this.printError(TranslatableComponent.of("worldedit.version.bukkit.unsupported-adapter", TextComponent.of("https://enginehub.org/worldedit/#downloads", TextColor.AQUA).clickEvent(ClickEvent.openUrl("https://enginehub.org/worldedit/#downloads"))));
        }
    }

    @Override
    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        return null;
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKeyImpl(this.player);
    }

    @Override
    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 pos, B block) {
        Location loc = new Location(this.player.getWorld(), (double)pos.x(), (double)pos.y(), (double)pos.z());
        if (block == null) {
            this.player.sendBlockChange(loc, this.player.getWorld().getBlockAt(loc).getBlockData());
        } else {
            LinCompoundTag nbt;
            this.player.sendBlockChange(loc, BukkitAdapter.adapt(block));
            BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
            if (adapter != null && block.getBlockType() == BlockTypes.STRUCTURE_BLOCK && block instanceof BaseBlock && (nbt = ((BaseBlock)block).getNbt()) != null) {
                adapter.sendFakeNBT(this.player, pos, nbt);
                adapter.sendFakeOP(this.player);
            }
        }
    }

    static class SessionKeyImpl
    implements SessionKey {
        private final UUID uuid;
        private final String name;

        SessionKeyImpl(Player player) {
            this.uuid = player.getUniqueId();
            this.name = player.getName();
        }

        @Override
        public UUID getUniqueId() {
            return this.uuid;
        }

        @Override
        @Nullable
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isActive() {
            return Bukkit.getServer().getPlayer(this.uuid) != null;
        }

        @Override
        public boolean isPersistent() {
            return true;
        }
    }
}

