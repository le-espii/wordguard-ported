/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.formatting.component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.internal.command.CommandUtil;
import com.sk89q.worldedit.util.formatting.component.InvalidComponentException;
import com.sk89q.worldedit.util.formatting.component.MessageBox;
import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.List;
import javax.annotation.Nullable;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.util.HelpGenerator;

public class CommandUsageBox
extends TextComponentProducer {
    public CommandUsageBox(List<Command> commands, String commandString, String helpRootCommand) throws InvalidComponentException {
        this(commands, commandString, helpRootCommand, null);
    }

    public CommandUsageBox(List<Command> commands, String commandString, String helpRootCommand, @Nullable CommandParameters parameters) throws InvalidComponentException {
        Preconditions.checkNotNull(commands);
        Preconditions.checkNotNull((Object)commandString);
        Preconditions.checkNotNull((Object)helpRootCommand);
        this.attachCommandUsage(commands, commandString, helpRootCommand);
    }

    private void attachCommandUsage(List<Command> commands, String commandString, String helpRootCommand) {
        TextComponentProducer boxContent = new TextComponentProducer().append(HelpGenerator.create(commands).getFullHelp());
        if (CommandUtil.getSubCommands((Command)Iterables.getLast(commands)).size() > 0) {
            boxContent.append(TextComponent.newline()).append(ColorConfig.helpText().wrap(new Component[]{((TextComponent.Builder)TextComponent.builder("> ").append(ColorConfig.mainText().wrap(new Component[]{((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder("List Subcommands").decoration(TextDecoration.ITALIC, true)).clickEvent(ClickEvent.runCommand(helpRootCommand + " -s " + commandString))).hoverEvent(HoverEvent.showText(TextComponent.of("List all subcommands of this command")))).build()}))).build()}));
        }
        MessageBox box = new MessageBox("Help for " + commandString, boxContent);
        this.append(box.create());
    }
}

