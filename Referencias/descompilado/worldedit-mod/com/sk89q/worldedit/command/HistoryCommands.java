/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 */
package com.sk89q.worldedit.command;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class HistoryCommands {
    private final WorldEdit worldEdit;

    public HistoryCommands(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(name="undo", aliases={"/undo"}, desc="Undoes the last action (from history)")
    @CommandPermissions(value={"worldedit.history.undo", "worldedit.history.undo.self"})
    public void undo(Actor actor, LocalSession session, @Arg(desc="Number of undoes to perform", def={"1"}) int times, @Arg(name="player", desc="Undo this player's operations", def={""}) String playerName) throws WorldEditException {
        BlockBag blockBag;
        EditSession undone;
        times = Math.max(1, times);
        LocalSession undoSession = session;
        if (playerName != null) {
            actor.checkPermission("worldedit.history.undo.other");
            undoSession = this.worldEdit.getSessionManager().findByName(playerName);
            if (undoSession == null) {
                actor.printError(TranslatableComponent.of("worldedit.session.cant-find-session", TextComponent.of(playerName)));
                return;
            }
        }
        int timesUndone = 0;
        for (int i = 0; i < times && (undone = undoSession.undo(blockBag = actor instanceof Player ? undoSession.getBlockBag((Player)actor) : null, actor)) != null; ++i) {
            ++timesUndone;
            this.worldEdit.flushBlockBag(actor, undone);
        }
        if (timesUndone > 0) {
            actor.printInfo(TranslatableComponent.of("worldedit.undo.undone", TextComponent.of(timesUndone)));
        } else {
            actor.printError(TranslatableComponent.of("worldedit.undo.none"));
        }
    }

    @Command(name="redo", aliases={"/redo"}, desc="Redoes the last action (from history)")
    @CommandPermissions(value={"worldedit.history.redo", "worldedit.history.redo.self"})
    public void redo(Actor actor, LocalSession session, @Arg(desc="Number of redoes to perform", def={"1"}) int times, @Arg(name="player", desc="Redo this player's operations", def={""}) String playerName) throws WorldEditException {
        BlockBag blockBag;
        EditSession redone;
        times = Math.max(1, times);
        LocalSession redoSession = session;
        if (playerName != null) {
            actor.checkPermission("worldedit.history.redo.other");
            redoSession = this.worldEdit.getSessionManager().findByName(playerName);
            if (redoSession == null) {
                actor.printError(TranslatableComponent.of("worldedit.session.cant-find-session", TextComponent.of(playerName)));
                return;
            }
        }
        int timesRedone = 0;
        for (int i = 0; i < times && (redone = redoSession.redo(blockBag = actor instanceof Player ? redoSession.getBlockBag((Player)actor) : null, actor)) != null; ++i) {
            ++timesRedone;
            this.worldEdit.flushBlockBag(actor, redone);
        }
        if (timesRedone > 0) {
            actor.printInfo(TranslatableComponent.of("worldedit.redo.redone", TextComponent.of(timesRedone)));
        } else {
            actor.printError(TranslatableComponent.of("worldedit.redo.none"));
        }
    }

    @Command(name="clearhistory", aliases={"/clearhistory"}, desc="Clear your history")
    @CommandPermissions(value={"worldedit.history.clear"})
    public void clearHistory(Actor actor, LocalSession session) {
        session.clearHistory();
        actor.printInfo(TranslatableComponent.of("worldedit.clearhistory.cleared"));
    }
}

