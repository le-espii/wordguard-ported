/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.AbstractNonPlayerActor;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NeoForgeCommandSender
extends AbstractNonPlayerActor {
    private static final UUID DEFAULT_ID = UUID.fromString("a233eb4b-4cab-42cd-9fd9-7e7b9a3f74be");
    private final CommandSourceStack sender;

    public NeoForgeCommandSender(CommandSourceStack sender) {
        Preconditions.checkNotNull((Object)sender);
        Preconditions.checkArgument((!sender.isPlayer() ? 1 : 0) != 0, (Object)"Cannot wrap a player");
        this.sender = sender;
    }

    @Override
    public UUID getUniqueId() {
        return DEFAULT_ID;
    }

    @Override
    public String getName() {
        return this.sender.getTextName();
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
        this.sendMessage((Component)Component.Serializer.fromJson((String)GsonComponentSerializer.INSTANCE.serialize(WorldEditText.format(component, this.getLocale())), (HolderLookup.Provider)this.sender.registryAccess()));
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
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasPermission(String perm) {
        return true;
    }

    @Override
    public void checkPermission(String permission) {
    }

    @Override
    public Locale getLocale() {
        return WorldEdit.getInstance().getConfiguration().defaultLocale;
    }

    public CommandSourceStack getSender() {
        return this.sender;
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKey(){

            @Override
            @Nullable
            public String getName() {
                return NeoForgeCommandSender.this.sender.getTextName();
            }

            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public UUID getUniqueId() {
                return DEFAULT_ID;
            }
        };
    }
}

