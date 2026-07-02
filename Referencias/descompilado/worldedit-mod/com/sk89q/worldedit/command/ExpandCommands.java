/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 */
package com.sk89q.worldedit.command;

import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.ExpandCommandsRegistration;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.command.util.PermissionCondition;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.annotation.MultiDirection;
import com.sk89q.worldedit.internal.command.CommandRegistrationHandler;
import com.sk89q.worldedit.internal.command.CommandUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandManagerService;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.SubCommandPart;

@CommandContainer
public class ExpandCommands {
    public static void register(CommandRegistrationHandler registration, CommandManager commandManager, CommandManagerService commandManagerService) {
        CommandManager collect = commandManagerService.newCommandManager();
        registration.register(collect, ExpandCommandsRegistration.builder(), new ExpandCommands());
        org.enginehub.piston.Command expandBaseCommand = collect.getCommand("/expand").orElseThrow(() -> new IllegalStateException("No /expand command"));
        commandManager.register("/expand", command -> {
            command.condition(new PermissionCondition((Set<String>)ImmutableSet.of((Object)"worldedit.selection.expand")));
            command.addPart(SubCommandPart.builder(TranslatableComponent.of("vert"), TextComponent.of("Vertical expansion sub-command")).withCommands((Collection<org.enginehub.piston.Command>)ImmutableSet.of((Object)ExpandCommands.createVertCommand(commandManager))).optional().build());
            command.addParts((Iterable<CommandPart>)expandBaseCommand.getParts());
            command.action(expandBaseCommand.getAction());
            command.description(expandBaseCommand.getDescription());
        });
    }

    private static org.enginehub.piston.Command createVertCommand(CommandManager commandManager) {
        return commandManager.newCommand("vert").description(TranslatableComponent.of("worldedit.expand.description.vert")).action(parameters -> {
            ExpandCommands.expandVert(CommandUtil.requireIV(Key.of(LocalSession.class), "localSession", parameters), CommandUtil.requireIV(Key.of(Actor.class), "actor", parameters), CommandUtil.requireIV(Key.of(World.class), "world", parameters));
            return 1;
        }).build();
    }

    private static void expandVert(LocalSession session, Actor actor, World world) throws IncompleteRegionException {
        Region region = session.getSelection(world);
        int height = world.getMaxY() - world.getMinY();
        try {
            long oldSize = region.getVolume();
            region.expand(BlockVector3.at(0, height, 0), BlockVector3.at(0, -height, 0));
            session.getRegionSelector(world).learnChanges();
            long newSize = region.getVolume();
            session.getRegionSelector(world).explainRegionAdjust(actor, session);
            long changeSize = newSize - oldSize;
            actor.printInfo(TranslatableComponent.of("worldedit.expand.expanded.vert", TextComponent.of(changeSize)));
        }
        catch (RegionOperationException e) {
            actor.printError(TextComponent.of(e.getMessage()));
        }
    }

    @Command(name="/expand", desc="Expand the selection area")
    @Logging(value=Logging.LogMode.REGION)
    public void expand(Actor actor, World world, LocalSession session, @Arg(desc="Amount to expand the selection by, can be `vert` to expand to the whole vertical column") int amount, @Arg(desc="Amount to expand the selection by in the other direction", def={"0"}) int reverseAmount, @MultiDirection @Arg(desc="Direction to expand", def={"me"}) List<BlockVector3> direction) throws WorldEditException {
        Region region = session.getSelection(world);
        long oldSize = region.getVolume();
        if (reverseAmount == 0) {
            for (BlockVector3 dir : direction) {
                region.expand(dir.multiply(amount));
            }
        } else {
            for (BlockVector3 dir : direction) {
                region.expand(dir.multiply(amount), dir.multiply(-reverseAmount));
            }
        }
        session.getRegionSelector(world).learnChanges();
        long newSize = region.getVolume();
        session.getRegionSelector(world).explainRegionAdjust(actor, session);
        long changeSize = newSize - oldSize;
        actor.printInfo(TranslatableComponent.of("worldedit.expand.expanded", TextComponent.of(changeSize)));
    }
}

