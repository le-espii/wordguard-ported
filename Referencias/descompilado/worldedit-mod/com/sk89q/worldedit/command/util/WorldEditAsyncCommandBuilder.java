/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.util;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.AsyncCommandBuilder;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

public final class WorldEditAsyncCommandBuilder {
    private WorldEditAsyncCommandBuilder() {
    }

    @Deprecated
    public static void createAndSendMessage(Actor actor, Callable<Component> task, @Nullable String desc) {
        WorldEditAsyncCommandBuilder.createAndSendMessage(actor, task, desc != null ? TextComponent.of(desc) : null);
    }

    public static void createAndSendMessage(Actor actor, Callable<Component> task, @Nullable Component desc) {
        AsyncCommandBuilder<Component> builder = AsyncCommandBuilder.wrap(task, actor);
        if (desc != null) {
            builder.setDelayMessage(desc);
        }
        builder.onSuccess((String)null, actor::printInfo).onFailure((String)null, WorldEdit.getInstance().getPlatformManager().getPlatformCommandManager().getExceptionConverter()).buildAndExec(WorldEdit.getInstance().getExecutorService());
    }
}

