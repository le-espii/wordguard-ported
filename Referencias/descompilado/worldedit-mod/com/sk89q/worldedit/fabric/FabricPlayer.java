/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.class_124
 *  net.minecraft.class_1268
 *  net.minecraft.class_1799
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_2561$class_2562
 *  net.minecraft.class_2591
 *  net.minecraft.class_2596
 *  net.minecraft.class_2622
 *  net.minecraft.class_2626
 *  net.minecraft.class_3218
 *  net.minecraft.class_3222
 *  net.minecraft.class_5250
 *  net.minecraft.class_7225$class_7874
 *  net.minecraft.class_8710
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricWorld;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.fabric.ThreadSafeCache;
import com.sk89q.worldedit.fabric.internal.NBTConverter;
import com.sk89q.worldedit.fabric.net.handler.WECUIPacketHandler;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2591;
import net.minecraft.class_2596;
import net.minecraft.class_2622;
import net.minecraft.class_2626;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5250;
import net.minecraft.class_7225;
import net.minecraft.class_8710;
import org.enginehub.linbus.tree.LinCompoundTag;

public class FabricPlayer
extends AbstractPlayerActor {
    private final class_3222 player;

    protected FabricPlayer(class_3222 player) {
        this.player = player;
        ThreadSafeCache.getInstance().getOnlineIds().add(this.getUniqueId());
    }

    @Override
    public UUID getUniqueId() {
        return this.player.method_5667();
    }

    @Override
    public BaseItemStack getItemInHand(HandSide handSide) {
        class_1799 is = this.player.method_5998(handSide == HandSide.MAIN_HAND ? class_1268.field_5808 : class_1268.field_5810);
        return FabricAdapter.adapt(is);
    }

    @Override
    public String getName() {
        return this.player.method_5477().getString();
    }

    @Override
    public BaseEntity getState() {
        throw new UnsupportedOperationException("Cannot create a state from this object");
    }

    @Override
    public Location getLocation() {
        Vector3 position = Vector3.at(this.player.method_23317(), this.player.method_23318(), this.player.method_23321());
        return new Location((Extent)FabricWorldEdit.inst.getWorld((class_1937)this.player.method_51469()), position, this.player.method_36454(), this.player.method_36455());
    }

    @Override
    public boolean setLocation(Location location) {
        class_3218 level = (class_3218)FabricAdapter.adapt((World)location.getExtent());
        this.player.method_14251(level, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return this.player.method_51469() == level;
    }

    @Override
    public World getWorld() {
        return FabricWorldEdit.inst.getWorld((class_1937)this.player.method_51469());
    }

    @Override
    public void giveItem(BaseItemStack itemStack) {
        this.player.method_31548().method_7394(FabricAdapter.adapt(itemStack));
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
        String[] params = event.getParameters();
        Object send = event.getTypeId();
        if (params.length > 0) {
            send = (String)send + "|" + StringUtil.joinString(params, "|");
        }
        ServerPlayNetworking.send((class_3222)this.player, (class_8710)new WECUIPacketHandler.CuiPacket((String)send));
    }

    @Override
    public Locale getLocale() {
        return TextUtils.getLocaleByMinecraftTag(this.player.method_53823().comp_1951());
    }

    @Override
    @Deprecated
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            this.player.method_43496((class_2561)class_2561.method_43470((String)part));
        }
    }

    @Override
    @Deprecated
    public void printDebug(String msg) {
        this.sendColorized(msg, class_124.field_1080);
    }

    @Override
    @Deprecated
    public void print(String msg) {
        this.sendColorized(msg, class_124.field_1076);
    }

    @Override
    @Deprecated
    public void printError(String msg) {
        this.sendColorized(msg, class_124.field_1061);
    }

    @Override
    public void print(Component component) {
        this.player.method_43496((class_2561)class_2561.class_2562.method_10877((String)GsonComponentSerializer.INSTANCE.serialize(WorldEditText.format(component, this.getLocale())), (class_7225.class_7874)this.player.method_37908().method_30349()));
    }

    private void sendColorized(String msg, class_124 formatting) {
        for (String part : msg.split("\n")) {
            class_5250 component = class_2561.method_43470((String)part).method_27694(style -> style.method_10977(formatting));
            this.player.method_43496((class_2561)component);
        }
    }

    @Override
    public boolean trySetPosition(Vector3 pos, float pitch, float yaw) {
        this.player.field_13987.method_14363(pos.x(), pos.y(), pos.z(), yaw, pitch);
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
        return FabricWorldEdit.inst.getPermissionsProvider().hasPermission(this.player, perm);
    }

    @Override
    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        return null;
    }

    @Override
    public boolean isAllowedToFly() {
        return this.player.method_31549().field_7478;
    }

    @Override
    public void setFlying(boolean flying) {
        if (this.player.method_31549().field_7479 != flying) {
            this.player.method_31549().field_7479 = flying;
            this.player.method_7355();
        }
    }

    @Override
    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 pos, B block) {
        World world = this.getWorld();
        if (!(world instanceof FabricWorld)) {
            return;
        }
        class_2338 loc = FabricAdapter.toBlockPos(pos);
        if (block == null) {
            class_2626 packetOut = new class_2626((class_1922)((FabricWorld)world).getWorld(), loc);
            this.player.field_13987.method_14364((class_2596)packetOut);
        } else {
            LinCompoundTag nbtData;
            class_2626 packetOut = new class_2626(loc, FabricAdapter.adapt(block.toImmutableState()));
            this.player.field_13987.method_14364((class_2596)packetOut);
            if (block instanceof BaseBlock && block.getBlockType().equals(BlockTypes.STRUCTURE_BLOCK) && (nbtData = ((BaseBlock)block).getNbt()) != null) {
                this.player.field_13987.method_14364((class_2596)new class_2622(new class_2338(pos.x(), pos.y(), pos.z()), class_2591.field_11895, NBTConverter.toNative(nbtData)));
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

        SessionKeyImpl(class_3222 player) {
            this.uuid = player.method_5667();
            this.name = player.method_5477().getString();
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

