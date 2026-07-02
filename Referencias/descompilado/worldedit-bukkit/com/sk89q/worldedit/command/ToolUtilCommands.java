/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 */
package com.sk89q.worldedit.command;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class ToolUtilCommands {
    private final WorldEdit we;

    public ToolUtilCommands(WorldEdit we) {
        this.we = we;
    }

    @Command(name="/", aliases={","}, desc="Toggle the super pickaxe function")
    @CommandPermissions(value={"worldedit.superpickaxe"})
    public void togglePickaxe(Player player, LocalSession session, @Arg(desc="The new super pickaxe state", def={""}) Boolean superPickaxe) {
        boolean hasSuperPickAxe = session.hasSuperPickAxe();
        if (superPickaxe != null && superPickaxe == hasSuperPickAxe) {
            player.printError(TranslatableComponent.of(superPickaxe != false ? "worldedit.tool.superpickaxe.enabled.already" : "worldedit.tool.superpickaxe.disabled.already"));
            return;
        }
        if (hasSuperPickAxe) {
            session.disableSuperPickAxe();
            player.printInfo(TranslatableComponent.of("worldedit.tool.superpickaxe.disabled"));
        } else {
            session.enableSuperPickAxe();
            player.printInfo(TranslatableComponent.of("worldedit.tool.superpickaxe.enabled"));
        }
    }

    @Command(name="mask", desc="Set the brush mask")
    @CommandPermissions(value={"worldedit.brush.options.mask"})
    public void mask(Player player, LocalSession session, @Arg(desc="The mask to set", def={""}) Mask mask) throws WorldEditException {
        BrushTool brushTool = session.getBrush(player.getItemInHand(HandSide.MAIN_HAND).getType());
        if (brushTool == null) {
            player.printError(TranslatableComponent.of("worldedit.brush.none.equipped"));
            return;
        }
        brushTool.setMask(mask);
        if (mask == null) {
            player.printInfo(TranslatableComponent.of("worldedit.tool.mask.disabled"));
        } else {
            player.printInfo(TranslatableComponent.of("worldedit.tool.mask.set"));
        }
    }

    @Command(name="material", aliases={"/material"}, desc="Set the brush material")
    @CommandPermissions(value={"worldedit.brush.options.material"})
    public void material(Player player, LocalSession session, @Arg(desc="The pattern of blocks to use") Pattern pattern) throws WorldEditException {
        BrushTool brushTool = session.getBrush(player.getItemInHand(HandSide.MAIN_HAND).getType());
        if (brushTool == null) {
            player.printError(TranslatableComponent.of("worldedit.brush.none.equipped"));
            return;
        }
        brushTool.setFill(pattern);
        player.printInfo(TranslatableComponent.of("worldedit.tool.material.set"));
    }

    @Command(name="range", desc="Set the brush range")
    @CommandPermissions(value={"worldedit.brush.options.range"})
    public void range(Player player, LocalSession session, @Arg(desc="The range of the brush") int range) throws WorldEditException {
        BrushTool brushTool = session.getBrush(player.getItemInHand(HandSide.MAIN_HAND).getType());
        if (brushTool == null) {
            player.printError(TranslatableComponent.of("worldedit.brush.none.equipped"));
            return;
        }
        brushTool.setRange(range);
        player.printInfo(TranslatableComponent.of("worldedit.tool.range.set"));
    }

    @Command(name="size", desc="Set the brush size")
    @CommandPermissions(value={"worldedit.brush.options.size"})
    public void size(Player player, LocalSession session, @Arg(desc="The size of the brush") int size) throws WorldEditException {
        this.we.checkMaxBrushRadius(size);
        BrushTool brushTool = session.getBrush(player.getItemInHand(HandSide.MAIN_HAND).getType());
        if (brushTool == null) {
            player.printError(TranslatableComponent.of("worldedit.brush.none.equipped"));
            return;
        }
        brushTool.setSize(size);
        player.printInfo(TranslatableComponent.of("worldedit.tool.size.set"));
    }

    @Command(name="tracemask", desc="Set the mask used to stop tool traces")
    @CommandPermissions(value={"worldedit.brush.options.tracemask"})
    public void traceMask(Player player, LocalSession session, @Arg(desc="The trace mask to set", def={""}) Mask mask) throws WorldEditException {
        BrushTool brushTool = session.getBrush(player.getItemInHand(HandSide.MAIN_HAND).getType());
        if (brushTool == null) {
            player.printError(TranslatableComponent.of("worldedit.brush.none.equipped"));
            return;
        }
        brushTool.setTraceMask(mask);
        if (mask == null) {
            player.printInfo(TranslatableComponent.of("worldedit.tool.tracemask.disabled"));
        } else {
            player.printInfo(TranslatableComponent.of("worldedit.tool.tracemask.set"));
        }
    }
}

