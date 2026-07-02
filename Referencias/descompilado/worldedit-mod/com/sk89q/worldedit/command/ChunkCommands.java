/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonIOException
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.ArgFlag
 */
package com.sk89q.worldedit.command;

import com.google.common.base.Preconditions;
import com.google.gson.JsonIOException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.command.util.WorldEditAsyncCommandBuilder;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.anvil.ChunkDeleter;
import com.sk89q.worldedit.internal.anvil.ChunkDeletionInfo;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.storage.LegacyChunkStore;
import com.sk89q.worldedit.world.storage.McRegionChunkStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.exception.StopExecutionException;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class ChunkCommands {
    private final WorldEdit worldEdit;

    public ChunkCommands(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(name="chunkinfo", desc="Get information about the chunk you're inside")
    @CommandPermissions(value={"worldedit.chunkinfo"})
    public void chunkInfo(Player player) {
        Location pos = player.getBlockLocation();
        int chunkX = (int)Math.floor((double)pos.getBlockX() / 16.0);
        int chunkZ = (int)Math.floor((double)pos.getBlockZ() / 16.0);
        BlockVector2 chunkPos = BlockVector2.at(chunkX, chunkZ);
        player.printInfo(TranslatableComponent.of("worldedit.chunkinfo.chunk", TextComponent.of(chunkX), TextComponent.of(chunkZ)));
        player.printInfo(TranslatableComponent.of("worldedit.chunkinfo.old-filename", TextComponent.of(LegacyChunkStore.getFilename(chunkPos))));
        player.printInfo(TranslatableComponent.of("worldedit.chunkinfo.mcregion-filename", TextComponent.of(McRegionChunkStore.getFilename(chunkPos))));
    }

    @Command(name="listchunks", desc="List chunks that your selection includes")
    @CommandPermissions(value={"worldedit.listchunks"})
    public void listChunks(Actor actor, World world, LocalSession session, @ArgFlag(name=112, desc="Page number.", def={"1"}) int page) throws WorldEditException {
        Region region = session.getSelection(world);
        WorldEditAsyncCommandBuilder.createAndSendMessage(actor, () -> new ChunkListPaginationBox(region).create(page), TranslatableComponent.of("worldedit.listchunks.listfor", TextComponent.of(actor.getName())));
    }

    @Command(name="delchunks", desc="Delete chunks that your selection includes")
    @CommandPermissions(value={"worldedit.delchunks"})
    @Logging(value=Logging.LogMode.REGION)
    public void deleteChunks(Actor actor, World world, LocalSession session, @ArgFlag(name=111, desc="Only delete chunks older than the specified time.") ZonedDateTime beforeTime) throws WorldEditException {
        Path worldDir = world.getStoragePath();
        if (worldDir == null) {
            throw new StopExecutionException(TextComponent.of("Couldn't find world folder for this world."));
        }
        Path chunkPath = this.worldEdit.getWorkingDirectoryPath("delete_chunks.json");
        ChunkDeletionInfo currentInfo = null;
        if (Files.exists(chunkPath, new LinkOption[0])) {
            try {
                currentInfo = ChunkDeleter.readInfo(chunkPath);
            }
            catch (IOException e) {
                throw new StopExecutionException(TextComponent.of("Error reading existing chunk file."));
            }
        }
        if (currentInfo == null) {
            currentInfo = new ChunkDeletionInfo();
            currentInfo.batches = new ArrayList<ChunkDeletionInfo.ChunkBatch>();
        }
        ChunkDeletionInfo.ChunkBatch newBatch = new ChunkDeletionInfo.ChunkBatch();
        newBatch.worldPath = worldDir.toAbsolutePath().normalize().toString();
        newBatch.backup = true;
        Region selection = session.getSelection(world);
        if (selection instanceof CuboidRegion) {
            newBatch.minChunk = selection.getMinimumPoint().shr(4).toBlockVector2();
            newBatch.maxChunk = selection.getMaximumPoint().shr(4).toBlockVector2();
        } else {
            Set<BlockVector2> chunks = selection.getChunks();
            newBatch.chunks = new ArrayList<BlockVector2>(chunks);
        }
        if (beforeTime != null) {
            newBatch.deletionPredicates = new ArrayList<ChunkDeletionInfo.DeletionPredicate>();
            ChunkDeletionInfo.DeletionPredicate timePred = new ChunkDeletionInfo.DeletionPredicate();
            timePred.property = "modification";
            timePred.comparison = "<";
            timePred.value = String.valueOf((int)beforeTime.toOffsetDateTime().toEpochSecond());
            newBatch.deletionPredicates.add(timePred);
        }
        currentInfo.batches.add(newBatch);
        try {
            ChunkDeleter.writeInfo(currentInfo, chunkPath);
        }
        catch (JsonIOException | IOException e) {
            throw new StopExecutionException(TextComponent.of("Failed to write chunk list: " + e.getMessage()));
        }
        actor.print(TextComponent.of(String.format("%d chunk(s) have been marked for deletion the next time the server starts.", newBatch.getChunkCount())));
        if (currentInfo.batches.size() > 1) {
            actor.printDebug(TextComponent.of(String.format("%d chunks total marked for deletion. (May have overlaps).", currentInfo.batches.stream().mapToInt(ChunkDeletionInfo.ChunkBatch::getChunkCount).sum())));
        }
        actor.print(TextComponent.of("You can mark more chunks for deletion, or to stop now, run: ", TextColor.LIGHT_PURPLE).append(TextComponent.of("/stop", TextColor.AQUA).clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/stop"))));
    }

    private static class ChunkListPaginationBox
    extends PaginationBox {
        private final List<BlockVector2> chunks;

        ChunkListPaginationBox(Region region) {
            super("Selected Chunks", "/listchunks -p %page%");
            this.chunks = new ArrayList<BlockVector2>(region.getChunks());
        }

        @Override
        public Component getComponent(int number) {
            return TextComponent.of(this.chunks.get(number).toString());
        }

        @Override
        public int getComponentsSize() {
            return this.chunks.size();
        }
    }
}

