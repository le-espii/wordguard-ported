/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.scripting;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.InsufficientArgumentsException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.expression.invoke.ReturnException;
import com.sk89q.worldedit.scripting.CraftScriptEnvironment;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CraftScriptContext
extends CraftScriptEnvironment {
    private final List<EditSession> editSessions = new ArrayList<EditSession>();
    private final String[] args;

    public CraftScriptContext(WorldEdit controller, Platform server, LocalConfiguration config, LocalSession session, Player player, String[] args) {
        super(controller, server, config, session, player);
        this.args = args;
    }

    public EditSession remember() {
        EditSession editSession = this.controller.newEditSessionBuilder().locatableActor(this.player).maxBlocks(this.session.getBlockChangeLimit()).blockBag(this.session.getBlockBag(this.player)).build();
        Request.request().setEditSession(editSession);
        editSession.enableStandardMode();
        this.editSessions.add(editSession);
        return editSession;
    }

    public Player getPlayer() {
        return this.player;
    }

    public LocalSession getSession() {
        return this.session;
    }

    public LocalConfiguration getConfiguration() {
        return this.config;
    }

    public List<EditSession> getEditSessions() {
        return Collections.unmodifiableList(this.editSessions);
    }

    public void print(String message) {
        this.player.printInfo(TextComponent.of(message));
    }

    public void error(String message) {
        this.player.printError(TextComponent.of(message));
    }

    public void printRaw(String message) {
        this.player.print(TextComponent.of(message));
    }

    public void checkArgs(int min, int max, String usage) throws InsufficientArgumentsException {
        if (this.args.length <= min || max != -1 && this.args.length - 1 > max) {
            throw new InsufficientArgumentsException(TranslatableComponent.of("worldedit.error.incorrect-usage", TextComponent.of(usage)));
        }
    }

    public void exit() {
        throw new ReturnException(null);
    }

    public BaseBlock getBlock(String input, boolean allAllowed) throws WorldEditException {
        ParserContext context = new ParserContext();
        context.setActor(this.player);
        context.setWorld(this.player.getWorld());
        context.setSession(this.session);
        context.setRestricted(!allAllowed);
        context.setPreferringWildcard(false);
        return this.controller.getBlockFactory().parseFromListInput(input, context).stream().findFirst().orElse(null);
    }

    public BaseBlock getBlock(String id) throws WorldEditException {
        return this.getBlock(id, false);
    }

    public Pattern getBlockPattern(String list) throws WorldEditException {
        ParserContext context = new ParserContext();
        context.setActor(this.player);
        context.setWorld(this.player.getWorld());
        context.setSession(this.session);
        return (Pattern)this.controller.getPatternFactory().parseFromInput(list, context);
    }

    public Set<BaseBlock> getBlocks(String list, boolean allBlocksAllowed) throws WorldEditException {
        ParserContext context = new ParserContext();
        context.setActor(this.player);
        context.setWorld(this.player.getWorld());
        context.setSession(this.session);
        context.setRestricted(!allBlocksAllowed);
        return this.controller.getBlockFactory().parseFromListInput(list, context);
    }

    public File getSafeOpenFile(String folder, String filename, String defaultExt, String ... exts) throws FilenameException {
        File dir = this.controller.getWorkingDirectoryPath(folder).toFile();
        return this.controller.getSafeOpenFile(this.player, dir, filename, defaultExt, exts);
    }

    public File getSafeSaveFile(String folder, String filename, String defaultExt, String ... exts) throws FilenameException {
        File dir = this.controller.getWorkingDirectoryPath(folder).toFile();
        return this.controller.getSafeSaveFile(this.player, dir, filename, defaultExt, exts);
    }
}

