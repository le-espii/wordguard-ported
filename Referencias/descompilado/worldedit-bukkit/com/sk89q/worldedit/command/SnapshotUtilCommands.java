/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 */
package com.sk89q.worldedit.command;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.LegacySnapshotUtilCommands;
import com.sk89q.worldedit.command.SnapshotCommands;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.snapshot.experimental.Snapshot;
import com.sk89q.worldedit.world.snapshot.experimental.SnapshotRestore;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class SnapshotUtilCommands {
    private final WorldEdit we;
    private final LegacySnapshotUtilCommands legacy;

    public SnapshotUtilCommands(WorldEdit we) {
        this.we = we;
        this.legacy = new LegacySnapshotUtilCommands(we);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Command(name="restore", aliases={"/restore"}, desc="Restore the selection from a snapshot")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.snapshots.restore"})
    public void restore(Actor actor, World world, LocalSession session, EditSession editSession, @Arg(name="snapshot", desc="The snapshot to restore", def={""}) String snapshotName) throws WorldEditException, IOException {
        Snapshot snapshot;
        LocalConfiguration config = this.we.getConfiguration();
        SnapshotCommands.checkSnapshotsConfigured(config);
        if (config.snapshotRepo != null) {
            this.legacy.restore(actor, world, session, editSession, snapshotName);
            return;
        }
        Region region = session.getSelection(world);
        if (snapshotName != null) {
            URI uri = SnapshotCommands.resolveSnapshotName(config, snapshotName);
            Optional<Snapshot> snapOpt = config.snapshotDatabase.getSnapshot(uri);
            if (snapOpt.isEmpty()) {
                actor.printError(TranslatableComponent.of("worldedit.restore.not-available"));
                return;
            }
            snapshot = snapOpt.get();
        } else {
            snapshot = session.getSnapshotExperimental();
        }
        if (snapshot == null) {
            try (Stream<Snapshot> snapshotStream = config.snapshotDatabase.getSnapshotsNewestFirst(world.getName());){
                snapshot = snapshotStream.findFirst().orElse(null);
            }
            if (snapshot == null) {
                actor.printError(TranslatableComponent.of("worldedit.restore.none-for-specific-world", TextComponent.of(world.getName())));
                return;
            }
        }
        actor.printInfo(TranslatableComponent.of("worldedit.restore.loaded", TextComponent.of(snapshot.getInfo().getDisplayName())));
        try {
            SnapshotRestore restore = new SnapshotRestore(snapshot, editSession, region);
            restore.restore();
            if (restore.hadTotalFailure()) {
                String error = restore.getLastErrorMessage();
                if (!restore.getMissingChunks().isEmpty()) {
                    actor.printError(TranslatableComponent.of("worldedit.restore.chunk-not-present"));
                } else if (error != null) {
                    actor.printError(TranslatableComponent.of("worldedit.restore.block-place-failed"));
                    actor.printError(TranslatableComponent.of("worldedit.restore.block-place-error", TextComponent.of(error)));
                } else {
                    actor.printError(TranslatableComponent.of("worldedit.restore.chunk-load-failed"));
                }
            } else {
                actor.printInfo(TranslatableComponent.of("worldedit.restore.restored", TextComponent.of(restore.getMissingChunks().size()), TextComponent.of(restore.getErrorChunks().size())));
            }
        }
        finally {
            try {
                snapshot.close();
            }
            catch (IOException iOException) {}
        }
    }
}

