/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.tool;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class BlockDataCyler
implements DoubleActionBlockTool {
    private final Map<UUID, Property<?>> selectedProperties = new HashMap();

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.tool.data-cycler");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleCycle(LocalConfiguration config, Player player, LocalSession session, Location clicked, boolean forward) {
        World world = (World)clicked.getExtent();
        BlockVector3 blockPoint = clicked.toVector().toBlockPoint();
        BaseBlock block = world.getFullBlock(blockPoint);
        if (!(config.allowedDataCycleBlocks.isEmpty() || player.hasPermission("worldedit.override.data-cycler") || config.allowedDataCycleBlocks.contains(block.getBlockType().id()))) {
            player.printError(TranslatableComponent.of("worldedit.tool.data-cycler.block-not-permitted"));
            return true;
        }
        if (block.getStates().keySet().isEmpty()) {
            player.printError(TranslatableComponent.of("worldedit.tool.data-cycler.cant-cycle"));
        } else {
            Property currentProperty = this.selectedProperties.get(player.getUniqueId());
            if (currentProperty == null || forward && block.getState(currentProperty) == null) {
                currentProperty = (Property)block.getStates().keySet().stream().findFirst().get();
                this.selectedProperties.put(player.getUniqueId(), currentProperty);
            }
            if (forward) {
                block.getState(currentProperty);
                int index = currentProperty.getValues().indexOf(block.getState(currentProperty));
                index = (index + 1) % currentProperty.getValues().size();
                Property objProp = currentProperty;
                BlockStateHolder newBlock = block.with(objProp, currentProperty.getValues().get(index));
                try (EditSession editSession = session.createEditSession(player);){
                    editSession.disableBuffering();
                    try {
                        editSession.setBlock(blockPoint, newBlock);
                        player.printInfo(TranslatableComponent.of("worldedit.tool.data-cycler.new-value", TextComponent.of(currentProperty.getName()), TextComponent.of(String.valueOf(currentProperty.getValues().get(index)))));
                    }
                    catch (MaxChangedBlocksException e) {
                        player.printError(TranslatableComponent.of("worldedit.tool.max-block-changes"));
                    }
                    finally {
                        session.remember(editSession);
                    }
                }
            } else {
                ArrayList properties = Lists.newArrayList(block.getStates().keySet());
                int index = properties.indexOf(currentProperty);
                index = (index + 1) % properties.size();
                currentProperty = (Property)properties.get(index);
                this.selectedProperties.put(player.getUniqueId(), currentProperty);
                player.printInfo(TranslatableComponent.of("worldedit.tool.data-cycler.cycling", TextComponent.of(currentProperty.getName())));
            }
        }
        return true;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return this.handleCycle(config, player, session, clicked, true);
    }

    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return this.handleCycle(config, player, session, clicked, false);
    }
}

