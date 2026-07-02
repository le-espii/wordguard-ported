/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.command.tool.BlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import javax.annotation.Nullable;

public class QueryTool
implements BlockTool {
    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.tool.info");
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        int[] legacy;
        World world = (World)clicked.getExtent();
        BlockVector3 blockPoint = clicked.toVector().toBlockPoint();
        BaseBlock block = world.getFullBlock(blockPoint);
        TextComponent.Builder builder = TextComponent.builder();
        builder.append((Component)TextComponent.of("@" + String.valueOf((Object)clicked.toVector().toBlockPoint()) + ": ", TextColor.BLUE));
        builder.append(block.getBlockType().getRichName().color(TextColor.YELLOW));
        builder.append(TextComponent.of(" (" + String.valueOf(block) + ") ", TextColor.GRAY).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TranslatableComponent.of("worldedit.tool.info.blockstate.hover"))));
        int internalId = BlockStateIdAccess.getBlockStateId(block.toImmutableState());
        if (BlockStateIdAccess.isValidInternalId(internalId)) {
            builder.append(TextComponent.of(" (" + internalId + ") ", TextColor.DARK_GRAY).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TranslatableComponent.of("worldedit.tool.info.internalid.hover"))));
        }
        if ((legacy = LegacyMapper.getInstance().getLegacyFromBlock(block.toImmutableState())) != null) {
            builder.append(TextComponent.of(" (" + legacy[0] + ":" + legacy[1] + ") ", TextColor.DARK_GRAY).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TranslatableComponent.of("worldedit.tool.info.legacy.hover"))));
        }
        builder.append(TextComponent.of(" (" + world.getBlockLightLevel(blockPoint) + "/" + world.getBlockLightLevel(blockPoint.add(0, 1, 0)) + ")", TextColor.WHITE).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TranslatableComponent.of("worldedit.tool.info.light.hover"))));
        player.print((Component)builder.build());
        return true;
    }
}

