/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.paste;

import com.sk89q.worldedit.command.util.AsyncCommandBuilder;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.paste.EngineHubPaste;
import com.sk89q.worldedit.util.paste.PasteMetadata;
import com.sk89q.worldedit.util.paste.Paster;
import com.sk89q.worldedit.util.paste.Pasters;
import com.sk89q.worldedit.util.task.Supervisor;
import java.net.URL;
import java.util.concurrent.Callable;

public final class ActorCallbackPaste {
    private static final Paster paster = new EngineHubPaste();

    private ActorCallbackPaste() {
    }

    @Deprecated
    public static void pastebin(Supervisor supervisor, Actor sender, String content, String successMessage) {
        Callable<URL> task = paster.paste(content);
        AsyncCommandBuilder.wrap(task, sender).registerWithSupervisor(supervisor, "Submitting content to a pastebin service.").sendMessageAfterDelay("(Please wait... sending output to pastebin...)").onSuccess((String)null, url -> sender.print(String.format(successMessage, url))).onFailure("Failed to submit paste", null).buildAndExec(Pasters.getExecutor());
    }

    public static void pastebin(Supervisor supervisor, Actor sender, String content, TranslatableComponent.Builder successMessage) {
        Callable<URL> task = paster.paste(content);
        AsyncCommandBuilder.wrap(task, sender).registerWithSupervisor(supervisor, "Submitting content to a pastebin service.").setDelayMessage(TranslatableComponent.of("worldedit.pastebin.uploading")).onSuccess((String)null, url -> sender.printInfo((Component)successMessage.args((Component)TextComponent.of(url.toString())).build())).onFailure("Failed to submit paste", null).buildAndExec(Pasters.getExecutor());
    }

    public static void pastebin(Supervisor supervisor, Actor sender, String content, PasteMetadata pasteMetadata, TranslatableComponent.Builder successMessage) {
        Callable<URL> task = paster.paste(content, pasteMetadata);
        AsyncCommandBuilder.wrap(task, sender).registerWithSupervisor(supervisor, "Submitting content to a pastebin service.").setDelayMessage(TranslatableComponent.of("worldedit.pastebin.uploading")).onSuccess((String)null, url -> sender.printInfo((Component)successMessage.args((Component)TextComponent.of(url.toString())).build())).onFailure("Failed to submit paste", null).buildAndExec(Pasters.getExecutor());
    }
}

