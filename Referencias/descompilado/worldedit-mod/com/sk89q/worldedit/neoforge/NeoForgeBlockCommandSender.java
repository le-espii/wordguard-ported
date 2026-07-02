/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraft.world.level.BaseCommandBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.neoforged.fml.LogicalSide
 *  net.neoforged.neoforge.common.util.LogicalSidedProvider
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.AbstractCommandBlockActor;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;

public class NeoForgeBlockCommandSender
extends AbstractCommandBlockActor {
    private final BaseCommandBlock sender;
    private final UUID uuid;

    public NeoForgeBlockCommandSender(BaseCommandBlock sender) {
        super(new Location(NeoForgeAdapter.adapt(((BaseCommandBlock)Preconditions.checkNotNull((Object)sender)).getLevel()), NeoForgeAdapter.adapt(sender.getPosition())));
        this.sender = sender;
        this.uuid = UUID.nameUUIDFromBytes(("CMD" + String.valueOf(sender.getName())).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return this.sender.getName().getString();
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
        this.sendMessage((Component)Component.Serializer.fromJson((String)GsonComponentSerializer.INSTANCE.serialize(WorldEditText.format(component, this.getLocale())), (HolderLookup.Provider)this.sender.getLevel().registryAccess()));
    }

    private void sendColorized(String msg, ChatFormatting formatting) {
        for (String part : msg.split("\n")) {
            MutableComponent component = Component.literal((String)part);
            component.withStyle(formatting);
            this.sendMessage((Component)component);
        }
    }

    private void sendMessage(Component textComponent) {
        this.sender.sendSystemMessage(textComponent);
    }

    @Override
    public Locale getLocale() {
        return WorldEdit.getInstance().getConfiguration().defaultLocale;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public void checkPermission(String permission) throws AuthorizationException {
        if (!this.hasPermission(permission)) {
            throw new AuthorizationException();
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    public BaseCommandBlock getSender() {
        return this.sender;
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKey(){
            private volatile boolean active = true;

            private void updateActive() {
                BlockPos pos = new BlockPos((int)NeoForgeBlockCommandSender.this.sender.getPosition().x, (int)NeoForgeBlockCommandSender.this.sender.getPosition().y, (int)NeoForgeBlockCommandSender.this.sender.getPosition().z);
                int chunkX = SectionPos.blockToSectionCoord((int)pos.getX());
                int chunkZ = SectionPos.blockToSectionCoord((int)pos.getZ());
                if (!NeoForgeBlockCommandSender.this.sender.getLevel().getChunkSource().hasChunk(chunkX, chunkZ)) {
                    this.active = false;
                    return;
                }
                Block type = NeoForgeBlockCommandSender.this.sender.getLevel().getBlockState(pos).getBlock();
                this.active = type == Blocks.COMMAND_BLOCK || type == Blocks.CHAIN_COMMAND_BLOCK || type == Blocks.REPEATING_COMMAND_BLOCK;
            }

            @Override
            public String getName() {
                return NeoForgeBlockCommandSender.this.sender.getName().getString();
            }

            @Override
            public boolean isActive() {
                ((BlockableEventLoop)LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER)).submitAsync(this::updateActive);
                return this.active;
            }

            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public UUID getUniqueId() {
                return NeoForgeBlockCommandSender.this.uuid;
            }
        };
    }
}

