/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.NeoForgeWorld;
import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import com.sk89q.worldedit.neoforge.ThreadSafeCache;
import com.sk89q.worldedit.neoforge.internal.NBTConverter;
import com.sk89q.worldedit.neoforge.net.handler.WECUIPacketHandler;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.enginehub.linbus.tree.LinCompoundTag;

public class NeoForgePlayer
extends AbstractPlayerActor {
    private final ServerPlayer player;

    protected NeoForgePlayer(ServerPlayer player) {
        this.player = player;
        ThreadSafeCache.getInstance().getOnlineIds().add(this.getUniqueId());
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUUID();
    }

    @Override
    public BaseItemStack getItemInHand(HandSide handSide) {
        ItemStack is = this.player.getItemInHand(handSide == HandSide.MAIN_HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        return NeoForgeAdapter.adapt(is);
    }

    @Override
    public String getName() {
        return this.player.getName().getString();
    }

    @Override
    public BaseEntity getState() {
        throw new UnsupportedOperationException("Cannot create a state from this object");
    }

    @Override
    public Location getLocation() {
        Vector3 position = Vector3.at(this.player.getX(), this.player.getY(), this.player.getZ());
        return new Location((Extent)NeoForgeWorldEdit.inst.getWorld(this.player.serverLevel()), position, this.player.getYRot(), this.player.getXRot());
    }

    @Override
    public boolean setLocation(Location location) {
        ServerLevel level = NeoForgeAdapter.adapt((World)location.getExtent());
        this.player.teleportTo(level, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return this.player.serverLevel() == level;
    }

    @Override
    public World getWorld() {
        return NeoForgeWorldEdit.inst.getWorld(this.player.serverLevel());
    }

    @Override
    public void giveItem(BaseItemStack itemStack) {
        this.player.getInventory().add(NeoForgeAdapter.adapt(itemStack));
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
        String[] params = event.getParameters();
        Object send = event.getTypeId();
        if (params.length > 0) {
            send = (String)send + "|" + StringUtil.joinString(params, "|");
        }
        PacketDistributor.sendToPlayer((ServerPlayer)this.player, (CustomPacketPayload)new WECUIPacketHandler.CuiPacket((String)send), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    private void sendMessage(Component textComponent) {
        this.player.sendSystemMessage(textComponent);
    }

    @Override
    @Deprecated
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            this.sendMessage((Component)Component.literal((String)part));
        }
    }

    @Override
    @Deprecated
    public void printDebug(String msg) {
        this.sendColorized(msg, ChatFormatting.GRAY);
    }

    @Override
    @Deprecated
    public void print(String msg) {
        this.sendColorized(msg, ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    @Deprecated
    public void printError(String msg) {
        this.sendColorized(msg, ChatFormatting.RED);
    }

    @Override
    public void print(com.sk89q.worldedit.util.formatting.text.Component component) {
        this.sendMessage((Component)Component.Serializer.fromJson((String)GsonComponentSerializer.INSTANCE.serialize(WorldEditText.format(component, this.getLocale())), (HolderLookup.Provider)this.player.registryAccess()));
    }

    private void sendColorized(String msg, ChatFormatting formatting) {
        for (String part : msg.split("\n")) {
            MutableComponent component = Component.literal((String)part);
            component.withStyle(formatting);
            this.sendMessage((Component)component);
        }
    }

    @Override
    public boolean trySetPosition(Vector3 pos, float pitch, float yaw) {
        this.player.connection.teleport(pos.x(), pos.y(), pos.z(), yaw, pitch);
        return true;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public BlockBag getInventoryBlockBag() {
        return null;
    }

    @Override
    public boolean hasPermission(String perm) {
        return NeoForgeWorldEdit.inst.getPermissionsProvider().hasPermission(this.player, perm);
    }

    @Override
    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        return null;
    }

    @Override
    public boolean isAllowedToFly() {
        return this.player.mayFly();
    }

    @Override
    public void setFlying(boolean flying) {
        if (this.player.getAbilities().flying != flying) {
            this.player.getAbilities().flying = flying;
            this.player.onUpdateAbilities();
        }
    }

    @Override
    public Locale getLocale() {
        return TextUtils.getLocaleByMinecraftTag(this.player.getLanguage());
    }

    @Override
    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 pos, B block) {
        World world = this.getWorld();
        if (!(world instanceof NeoForgeWorld)) {
            return;
        }
        BlockPos loc = NeoForgeAdapter.toBlockPos(pos);
        if (block == null) {
            ClientboundBlockUpdatePacket packetOut = new ClientboundBlockUpdatePacket((BlockGetter)((NeoForgeWorld)world).getWorld(), loc);
            this.player.connection.send((Packet)packetOut);
        } else {
            this.player.connection.send((Packet)new ClientboundBlockUpdatePacket(loc, NeoForgeAdapter.adapt(block.toImmutableState())));
            if (block instanceof BaseBlock) {
                LinCompoundTag nbtData;
                BaseBlock baseBlock = (BaseBlock)block;
                if (block.getBlockType().equals(BlockTypes.STRUCTURE_BLOCK) && (nbtData = baseBlock.getNbt()) != null) {
                    this.player.connection.send((Packet)new ClientboundBlockEntityDataPacket(new BlockPos(pos.x(), pos.y(), pos.z()), BlockEntityType.STRUCTURE_BLOCK, NBTConverter.toNative(nbtData)));
                }
            }
        }
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKeyImpl(this.player);
    }

    static class SessionKeyImpl
    implements SessionKey {
        private final UUID uuid;
        private final String name;

        SessionKeyImpl(ServerPlayer player) {
            this.uuid = player.getUUID();
            this.name = player.getName().getString();
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
            return ThreadSafeCache.getInstance().getOnlineIds().contains(this.uuid);
        }

        @Override
        public boolean isPersistent() {
            return true;
        }
    }
}

