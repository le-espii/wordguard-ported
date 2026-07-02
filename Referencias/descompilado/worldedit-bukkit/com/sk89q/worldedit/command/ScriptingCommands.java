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
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class ScriptingCommands {
    private final WorldEdit worldEdit;

    public ScriptingCommands(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(name="cs", desc="Execute a CraftScript")
    @CommandPermissions(value={"worldedit.scripting.execute"})
    @Logging(value=Logging.LogMode.ALL)
    public void execute(Player player, LocalSession session, @Arg(desc="Filename of the CraftScript to load") String filename, @Arg(desc="Arguments to the CraftScript", def={""}, variable=true) List<String> args) throws WorldEditException {
        if (!player.hasPermission("worldedit.scripting.execute." + filename)) {
            player.printError(TranslatableComponent.of("worldedit.execute.script-permissions"));
            return;
        }
        session.setLastScript(filename);
        Path dir = this.worldEdit.getWorkingDirectoryPath(this.worldEdit.getConfiguration().scriptsDir);
        File f = this.worldEdit.getSafeOpenFile(player, dir.toFile(), filename, "js", "js");
        this.worldEdit.runScript(player, f, (String[])Stream.concat(Stream.of(filename), args.stream()).toArray(String[]::new));
    }

    @Command(name=".s", desc="Execute last CraftScript")
    @CommandPermissions(value={"worldedit.scripting.execute"})
    @Logging(value=Logging.LogMode.ALL)
    public void executeLast(Player player, LocalSession session, @Arg(desc="Arguments to the CraftScript", def={""}, variable=true) List<String> args) throws WorldEditException {
        String lastScript = session.getLastScript();
        if (!player.hasPermission("worldedit.scripting.execute." + lastScript)) {
            player.printError(TranslatableComponent.of("worldedit.execute.script-permissions"));
            return;
        }
        if (lastScript == null) {
            player.printError(TranslatableComponent.of("worldedit.executelast.no-script"));
            return;
        }
        File dir = this.worldEdit.getWorkingDirectoryPath(this.worldEdit.getConfiguration().scriptsDir).toFile();
        File f = this.worldEdit.getSafeOpenFile(player, dir, lastScript, "js", "js");
        this.worldEdit.runScript(player, f, (String[])Stream.concat(Stream.of(lastScript), args.stream()).toArray(String[]::new));
    }
}

