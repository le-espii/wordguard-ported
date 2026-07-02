/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  net.minecraft.class_124
 *  net.minecraft.class_2168
 *  net.minecraft.class_2561
 *  net.minecraft.class_2561$class_2562
 *  net.minecraft.class_5250
 *  net.minecraft.class_7225$class_7874
 */
package com.sk89q.worldedit.fabric;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.AbstractNonPlayerActor;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_124;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_7225;

public class FabricCommandSender
extends AbstractNonPlayerActor {
    private static final UUID DEFAULT_ID = UUID.fromString("a233eb4b-4cab-42cd-9fd9-7e7b9a3f74be");
    private final class_2168 sender;

    public FabricCommandSender(class_2168 sender) {
        Preconditions.checkNotNull((Object)sender);
        Preconditions.checkArgument((!sender.method_43737() ? 1 : 0) != 0, (Object)"Cannot wrap a player");
        this.sender = sender;
    }

    @Override
    public UUID getUniqueId() {
        return DEFAULT_ID;
    }

    @Override
    public String getName() {
        return this.sender.method_9214();
    }

    @Override
    @Deprecated
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            this.sendMessage((class_2561)class_2561.method_43470((String)part));
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
        this.sendMessage((class_2561)class_2561.class_2562.method_10877((String)GsonComponentSerializer.INSTANCE.serialize(WorldEditText.format(component, this.getLocale())), (class_7225.class_7874)this.sender.method_30497()));
    }

    private void sendColorized(String msg, class_124 formatting) {
        for (String part : msg.split("\n")) {
            class_5250 component = class_2561.method_43470((String)part);
            component.method_27692(formatting);
            this.sendMessage((class_2561)component);
        }
    }

    private void sendMessage(class_2561 textComponent) {
        this.sender.method_45068(textComponent);
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

    public class_2168 getSender() {
        return this.sender;
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKey(){

            @Override
            @Nullable
            public String getName() {
                return FabricCommandSender.this.sender.method_9214();
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

