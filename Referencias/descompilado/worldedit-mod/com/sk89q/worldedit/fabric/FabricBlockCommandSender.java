/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.class_124
 *  net.minecraft.class_1918
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_2561$class_2562
 *  net.minecraft.class_4076
 *  net.minecraft.class_5250
 *  net.minecraft.class_7225$class_7874
 */
package com.sk89q.worldedit.fabric;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.AbstractCommandBlockActor;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.class_124;
import net.minecraft.class_1918;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_4076;
import net.minecraft.class_5250;
import net.minecraft.class_7225;

public class FabricBlockCommandSender
extends AbstractCommandBlockActor {
    private final class_1918 sender;
    private final UUID uuid;

    public FabricBlockCommandSender(class_1918 sender) {
        super(new Location(FabricAdapter.adapt((class_1937)((class_1918)Preconditions.checkNotNull((Object)sender)).method_8293()), FabricAdapter.adapt(sender.method_8300())));
        this.sender = sender;
        this.uuid = UUID.nameUUIDFromBytes(("CMD" + String.valueOf(sender.method_8299())).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return this.sender.method_8299().getString();
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
        this.sendMessage((class_2561)class_2561.class_2562.method_10877((String)GsonComponentSerializer.INSTANCE.serialize(WorldEditText.format(component, this.getLocale())), (class_7225.class_7874)this.sender.method_8293().method_30349()));
    }

    private void sendColorized(String msg, class_124 formatting) {
        for (String part : msg.split("\n")) {
            class_5250 component = class_2561.method_43470((String)part);
            component.method_27692(formatting);
            this.sendMessage((class_2561)component);
        }
    }

    private void sendMessage(class_2561 textComponent) {
        this.sender.method_43496(textComponent);
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

    public class_1918 getSender() {
        return this.sender;
    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKey(){
            private volatile boolean active = true;

            private void updateActive() {
                class_2338 pos = new class_2338((int)FabricBlockCommandSender.this.sender.method_8300().field_1352, (int)FabricBlockCommandSender.this.sender.method_8300().field_1351, (int)FabricBlockCommandSender.this.sender.method_8300().field_1350);
                int chunkX = class_4076.method_18675((int)pos.method_10263());
                int chunkZ = class_4076.method_18675((int)pos.method_10260());
                if (!FabricBlockCommandSender.this.sender.method_8293().method_14178().method_12123(chunkX, chunkZ)) {
                    this.active = false;
                    return;
                }
                class_2248 type = FabricBlockCommandSender.this.sender.method_8293().method_8320(pos).method_26204();
                this.active = type == class_2246.field_10525 || type == class_2246.field_10395 || type == class_2246.field_10263;
            }

            @Override
            public String getName() {
                return FabricBlockCommandSender.this.sender.method_8299().getString();
            }

            @Override
            public boolean isActive() {
                FabricBlockCommandSender.this.getSender().method_8293().method_8503().execute(this::updateActive);
                return this.active;
            }

            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public UUID getUniqueId() {
                return FabricBlockCommandSender.this.uuid;
            }
        };
    }
}

