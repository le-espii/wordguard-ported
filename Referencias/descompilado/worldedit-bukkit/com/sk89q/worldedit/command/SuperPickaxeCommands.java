/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 */
package com.sk89q.worldedit.command;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.tool.AreaPickaxe;
import com.sk89q.worldedit.command.tool.RecursivePickaxe;
import com.sk89q.worldedit.command.tool.SinglePickaxe;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class SuperPickaxeCommands {
    private final WorldEdit we;

    public SuperPickaxeCommands(WorldEdit we) {
        this.we = we;
    }

    @Command(name="single", desc="Enable the single block super pickaxe mode")
    @CommandPermissions(value={"worldedit.superpickaxe"})
    public void single(Player player, LocalSession session) throws WorldEditException {
        session.setSuperPickaxe(new SinglePickaxe());
        session.enableSuperPickAxe();
        player.printInfo(TranslatableComponent.of("worldedit.tool.superpickaxe.mode.single"));
    }

    @Command(name="area", desc="Enable the area super pickaxe pickaxe mode")
    @CommandPermissions(value={"worldedit.superpickaxe.area"})
    public void area(Player player, LocalSession session, @Arg(desc="The range of the area pickaxe") int range) throws WorldEditException {
        LocalConfiguration config = this.we.getConfiguration();
        if (range > config.maxSuperPickaxeSize) {
            player.printError(TranslatableComponent.of("worldedit.tool.superpickaxe.max-range", TextComponent.of(config.maxSuperPickaxeSize)));
            return;
        }
        session.setSuperPickaxe(new AreaPickaxe(range));
        session.enableSuperPickAxe();
        player.printInfo(TranslatableComponent.of("worldedit.tool.superpickaxe.mode.area"));
    }

    @Command(name="recursive", aliases={"recur"}, desc="Enable the recursive super pickaxe pickaxe mode")
    @CommandPermissions(value={"worldedit.superpickaxe.recursive"})
    public void recursive(Player player, LocalSession session, @Arg(desc="The range of the recursive pickaxe") double range) throws WorldEditException {
        LocalConfiguration config = this.we.getConfiguration();
        if (range > (double)config.maxSuperPickaxeSize) {
            player.printError(TranslatableComponent.of("worldedit.tool.superpickaxe.max-range", TextComponent.of(config.maxSuperPickaxeSize)));
            return;
        }
        session.setSuperPickaxe(new RecursivePickaxe(range));
        session.enableSuperPickAxe();
        player.printInfo(TranslatableComponent.of("worldedit.tool.superpickaxe.mode.recursive"));
    }
}

