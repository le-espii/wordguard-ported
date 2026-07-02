/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.sk89q.worldedit.util.formatting.component;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.component.SubtleFormat;
import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.util.List;

public class CommandListBox
extends PaginationBox {
    private final List<CommandEntry> commands = Lists.newArrayList();
    private final String helpCommand;
    private boolean hideHelp;

    public CommandListBox(String title, String pageCommand, String helpCommand) {
        super(title, pageCommand);
        this.helpCommand = helpCommand;
    }

    @Override
    public Component getComponent(int number) {
        return this.commands.get(number).createComponent(this.hideHelp);
    }

    @Override
    public int getComponentsSize() {
        return this.commands.size();
    }

    public void appendCommand(String alias, Component description) {
        this.appendCommand(alias, description, null);
    }

    @Deprecated
    public void appendCommand(String alias, String description, String insertion) {
        this.appendCommand(alias, TextComponent.of(description), insertion);
    }

    public void appendCommand(String alias, Component description, String insertion) {
        this.commands.add(new CommandEntry(alias, description, insertion));
    }

    public boolean isHidingHelp() {
        return this.hideHelp;
    }

    public void setHidingHelp(boolean hideHelp) {
        this.hideHelp = hideHelp;
    }

    private class CommandEntry {
        private final String alias;
        private final Component description;
        private final String insertion;

        CommandEntry(String alias, Component description, String insertion) {
            this.alias = alias;
            this.description = description;
            this.insertion = insertion;
        }

        Component createComponent(boolean hideHelp) {
            TextComponentProducer line = new TextComponentProducer();
            if (!hideHelp) {
                line.append(((TextComponent)SubtleFormat.wrap("? ").clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, CommandListBox.this.helpCommand + " " + this.insertion))).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Additional Help"))));
            }
            TextComponent command = TextComponent.of(this.alias, TextColor.GOLD);
            if (this.insertion == null) {
                line.append(command);
            } else {
                line.append(((TextComponent)command.clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, this.insertion))).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to select"))));
            }
            return line.append(TextComponent.of(": ")).append(this.description).create();
        }
    }
}

