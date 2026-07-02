/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.io.MoreFiles
 *  org.apache.logging.log4j.Logger
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.ArgFlag
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.MoreFiles;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.AsyncCommandBuilder;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.WorldEditAsyncCommandBuilder;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareDestination;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareMetadata;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.formatting.component.CodeFormat;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.component.SubtleFormat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.util.io.file.MorePaths;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.apache.logging.log4j.Logger;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.annotation.param.Switch;
import org.enginehub.piston.exception.CommandException;
import org.enginehub.piston.exception.StopExecutionException;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class SchematicCommands {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final WorldEdit worldEdit;

    public SchematicCommands(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(name="load", desc="Load a schematic into your clipboard")
    @CommandPermissions(value={"worldedit.clipboard.load", "worldedit.schematic.load"})
    public void load(Actor actor, LocalSession session, @Arg(desc="File name.") String filename, @Arg(desc="Format name.", def={"sponge"}) ClipboardFormat format) throws FilenameException {
        LocalConfiguration config = this.worldEdit.getConfiguration();
        File dir = this.worldEdit.getWorkingDirectoryPath(config.saveDir).toFile();
        File f = this.worldEdit.getSafeOpenFile(actor, dir, filename, BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getPrimaryFileExtension(), ClipboardFormats.getFileExtensionArray());
        if (!f.exists()) {
            actor.printError(TranslatableComponent.of("worldedit.schematic.load.does-not-exist", TextComponent.of(filename)));
            return;
        }
        ClipboardFormat inferredFormat = ClipboardFormats.findByFile(f);
        if (inferredFormat != null) {
            format = inferredFormat;
        }
        SchematicLoadTask task = new SchematicLoadTask(actor, f, format);
        AsyncCommandBuilder.wrap(task, actor).registerWithSupervisor(this.worldEdit.getSupervisor(), "Loading schematic " + filename).setDelayMessage(TranslatableComponent.of("worldedit.schematic.load.loading")).setWorkingMessage(TranslatableComponent.of("worldedit.schematic.load.still-loading")).onSuccess(((TextComponent)TextComponent.of(filename, TextColor.GOLD).append(TextComponent.of(" loaded. Paste it with ", TextColor.LIGHT_PURPLE))).append(CodeFormat.wrap("//paste").clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "//paste"))), session::setClipboard).onFailure("Failed to load schematic", this.worldEdit.getPlatformManager().getPlatformCommandManager().getExceptionConverter()).buildAndExec(this.worldEdit.getExecutorService());
    }

    @Command(name="save", desc="Save your clipboard into a schematic file")
    @CommandPermissions(value={"worldedit.clipboard.save", "worldedit.schematic.save"})
    public void save(Actor actor, LocalSession session, @Arg(desc="File name.") String filename, @Arg(desc="Format name.", def={"sponge"}) ClipboardFormat format, @Switch(name=102, desc="Overwrite an existing file.") boolean allowOverwrite) throws WorldEditException {
        File parent;
        if (this.worldEdit.getPlatformManager().queryCapability(Capability.GAME_HOOKS).getDataVersion() == -1) {
            actor.printError(TranslatableComponent.of("worldedit.schematic.unsupported-minecraft-version"));
            return;
        }
        LocalConfiguration config = this.worldEdit.getConfiguration();
        File dir = this.worldEdit.getWorkingDirectoryPath(config.saveDir).toFile();
        File f = this.worldEdit.getSafeSaveFile(actor, dir, filename, format.getPrimaryFileExtension(), new String[0]);
        boolean overwrite = f.exists();
        if (overwrite) {
            if (!actor.hasPermission("worldedit.schematic.delete")) {
                throw new StopExecutionException(TextComponent.of("That schematic already exists!"));
            }
            if (!allowOverwrite) {
                actor.printError(TranslatableComponent.of("worldedit.schematic.save.already-exists"));
                return;
            }
        }
        if ((parent = f.getParentFile()) != null && !parent.exists() && !parent.mkdirs()) {
            throw new StopExecutionException(TranslatableComponent.of("worldedit.schematic.save.failed-directory"));
        }
        ClipboardHolder holder = session.getClipboard();
        SchematicSaveTask task = new SchematicSaveTask(actor, f, format, holder, overwrite);
        AsyncCommandBuilder.wrap(task, actor).registerWithSupervisor(this.worldEdit.getSupervisor(), "Saving schematic " + filename).setDelayMessage(TranslatableComponent.of("worldedit.schematic.save.saving")).setWorkingMessage(TranslatableComponent.of("worldedit.schematic.save.still-saving")).onSuccess(filename + " saved" + (overwrite ? " (overwriting previous file)." : "."), null).onFailure("Failed to save schematic", this.worldEdit.getPlatformManager().getPlatformCommandManager().getExceptionConverter()).buildAndExec(this.worldEdit.getExecutorService());
    }

    @Command(name="share", desc="Share your clipboard as a schematic online")
    @CommandPermissions(value={"worldedit.clipboard.share", "worldedit.schematic.share"})
    public void share(Actor actor, LocalSession session, @Arg(desc="Schematic name. Defaults to name-millis", def={""}) String schematicName, @Arg(desc="Share location", def={"ehpaste"}) ClipboardShareDestination destination, @Arg(desc="Format name", def={""}) ClipboardFormat format) throws WorldEditException {
        if (this.worldEdit.getPlatformManager().queryCapability(Capability.GAME_HOOKS).getDataVersion() == -1) {
            actor.printError(TranslatableComponent.of("worldedit.schematic.unsupported-minecraft-version"));
            return;
        }
        if (format == null) {
            format = destination.getDefaultFormat();
        }
        if (!destination.supportsFormat(format)) {
            actor.printError(TranslatableComponent.of("worldedit.schematic.share.unsupported-format", TextComponent.of(destination.getName()), TextComponent.of(format.getName())));
            return;
        }
        ClipboardHolder holder = session.getClipboard();
        SchematicShareTask task = new SchematicShareTask(actor, holder, destination, format, schematicName);
        AsyncCommandBuilder.wrap(task, actor).registerWithSupervisor(this.worldEdit.getSupervisor(), "Sharing schematic").setDelayMessage(TranslatableComponent.of("worldedit.schematic.save.saving")).setWorkingMessage(TranslatableComponent.of("worldedit.schematic.save.still-saving")).onSuccess("Shared", consumer -> consumer.accept(actor)).onFailure("Failed to share schematic", this.worldEdit.getPlatformManager().getPlatformCommandManager().getExceptionConverter()).buildAndExec(this.worldEdit.getExecutorService());
    }

    @Command(name="delete", aliases={"d"}, desc="Delete a saved schematic")
    @CommandPermissions(value={"worldedit.schematic.delete"})
    public void delete(Actor actor, @Arg(desc="File name.") String filename) throws WorldEditException {
        LocalConfiguration config = this.worldEdit.getConfiguration();
        File dir = this.worldEdit.getWorkingDirectoryPath(config.saveDir).toFile();
        File f = this.worldEdit.getSafeOpenFile(actor, dir, filename, "schematic", ClipboardFormats.getFileExtensionArray());
        if (!f.exists()) {
            actor.printError(TranslatableComponent.of("worldedit.schematic.delete.does-not-exist", TextComponent.of(filename)));
            return;
        }
        if (!f.delete()) {
            actor.printError(TranslatableComponent.of("worldedit.schematic.delete.failed", TextComponent.of(filename)));
            return;
        }
        actor.printInfo(TranslatableComponent.of("worldedit.schematic.delete.deleted", TextComponent.of(filename)));
        try {
            LOGGER.info(actor.getName() + " deleted " + f.getCanonicalPath());
        }
        catch (IOException e) {
            LOGGER.info(actor.getName() + " deleted " + f.getAbsolutePath());
        }
    }

    @Command(name="formats", aliases={"listformats", "f"}, desc="List available formats")
    @CommandPermissions(value={"worldedit.schematic.formats"})
    public void formats(Actor actor) {
        actor.printInfo(TranslatableComponent.of("worldedit.schematic.formats.title"));
        boolean first = true;
        for (ClipboardFormat format : ClipboardFormats.getAll()) {
            StringBuilder builder = new StringBuilder();
            builder.append(format.getName()).append(": ");
            for (String lookupName : format.getAliases()) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append(lookupName);
                first = false;
            }
            first = true;
            actor.printInfo(TextComponent.of(builder.toString()));
        }
    }

    @Command(name="list", aliases={"all", "ls"}, desc="List saved schematics", descFooter="Note: Format is not fully verified until loading.")
    @CommandPermissions(value={"worldedit.schematic.list"})
    public void list(Actor actor, @ArgFlag(name=112, desc="Page to view.", def={"1"}) int page, @Switch(name=100, desc="Sort by date, oldest first") boolean oldFirst, @Switch(name=110, desc="Sort by date, newest first") boolean newFirst) {
        String flag;
        Comparator<Path> pathComparator;
        if (oldFirst && newFirst) {
            throw new StopExecutionException(TextComponent.of("Cannot sort by oldest and newest."));
        }
        String saveDir = this.worldEdit.getConfiguration().saveDir;
        if (oldFirst) {
            pathComparator = MorePaths.oldestFirst();
            flag = " -d";
        } else if (newFirst) {
            pathComparator = MorePaths.newestFirst();
            flag = " -n";
        } else {
            pathComparator = Comparator.naturalOrder();
            flag = "";
        }
        String pageCommand = actor.isPlayer() ? "//schem list -p %page%" + flag : null;
        WorldEditAsyncCommandBuilder.createAndSendMessage(actor, (Callable<Component>)new SchematicListTask(saveDir, pathComparator, page, pageCommand), SubtleFormat.wrap("(Please wait... gathering schematic list.)"));
    }

    private static List<Path> allFiles(Path root) throws IOException {
        ArrayList<Path> pathList = new ArrayList<Path>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root);){
            for (Path path : stream) {
                if (Files.isDirectory(path, new LinkOption[0])) {
                    pathList.addAll(SchematicCommands.allFiles(path));
                    continue;
                }
                pathList.add(path);
            }
        }
        return pathList;
    }

    private static class SchematicLoadTask
    implements Callable<ClipboardHolder> {
        private final Actor actor;
        private final File file;
        private final ClipboardFormat format;

        SchematicLoadTask(Actor actor, File file, ClipboardFormat format) {
            this.actor = actor;
            this.file = file;
            this.format = format;
        }

        @Override
        public ClipboardHolder call() throws Exception {
            try (Closer closer = Closer.create();){
                FileInputStream fis = closer.register(new FileInputStream(this.file));
                BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
                ClipboardReader reader = closer.register(this.format.getReader(bis));
                Clipboard clipboard = reader.read();
                LOGGER.info(this.actor.getName() + " loaded " + this.file.getCanonicalPath());
                ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
                return clipboardHolder;
            }
        }
    }

    private static class SchematicSaveTask
    extends SchematicOutputTask<Void> {
        private final Actor actor;
        private final File file;
        private final boolean overwrite;

        SchematicSaveTask(Actor actor, File file, ClipboardFormat format, ClipboardHolder holder, boolean overwrite) {
            super(format, holder);
            this.actor = actor;
            this.file = file;
            this.overwrite = overwrite;
        }

        @Override
        public Void call() throws Exception {
            try {
                this.writeToOutputStream(new FileOutputStream(this.file));
                LOGGER.info(this.actor.getName() + " saved " + this.file.getCanonicalPath() + (this.overwrite ? " (overwriting previous file)" : ""));
            }
            catch (IOException e) {
                this.file.delete();
                throw new CommandException(TextComponent.of(e.getMessage()), e, (ImmutableList<org.enginehub.piston.Command>)ImmutableList.of());
            }
            return null;
        }
    }

    private static class SchematicShareTask
    extends SchematicOutputTask<Consumer<Actor>> {
        private final Actor actor;
        private final String name;
        private final ClipboardShareDestination destination;

        SchematicShareTask(Actor actor, ClipboardHolder holder, ClipboardShareDestination destination, ClipboardFormat format, String name) {
            super(format, holder);
            this.actor = actor;
            this.name = name;
            this.destination = destination;
        }

        @Override
        public Consumer<Actor> call() throws Exception {
            ClipboardShareMetadata metadata = new ClipboardShareMetadata(this.format, (String)(this.name == null ? this.actor.getName() + "-" + System.currentTimeMillis() : this.name), this.actor.getName());
            return this.destination.share(metadata, this::writeToOutputStream);
        }
    }

    private static class SchematicListTask
    implements Callable<Component> {
        private final Comparator<Path> pathComparator;
        private final int page;
        private final Path rootDir;
        private final String pageCommand;

        SchematicListTask(String prefix, Comparator<Path> pathComparator, int page, String pageCommand) {
            this.pathComparator = pathComparator;
            this.page = page;
            this.rootDir = WorldEdit.getInstance().getWorkingDirectoryPath(prefix);
            this.pageCommand = pageCommand;
        }

        @Override
        public Component call() throws Exception {
            Path resolvedRoot = this.rootDir.toRealPath(new LinkOption[0]);
            List<Path> fileList = SchematicCommands.allFiles(resolvedRoot);
            if (fileList.isEmpty()) {
                return ErrorFormat.wrap("No schematics found.");
            }
            fileList.sort(this.pathComparator);
            SchematicPaginationBox paginationBox = new SchematicPaginationBox(resolvedRoot, fileList, this.pageCommand);
            return paginationBox.create(this.page);
        }
    }

    private static class SchematicPaginationBox
    extends PaginationBox {
        private final Path rootDir;
        private final List<Path> files;

        SchematicPaginationBox(Path rootDir, List<Path> files, String pageCommand) {
            super("Available schematics", pageCommand);
            this.rootDir = rootDir;
            this.files = files;
        }

        @Override
        public Component getComponent(int number) {
            Preconditions.checkArgument((number < this.files.size() && number >= 0 ? 1 : 0) != 0);
            Path file = this.files.get(number);
            String format = ClipboardFormats.getFileExtensionMap().get((Object)MoreFiles.getFileExtension((Path)file)).stream().findFirst().map(ClipboardFormat::getName).orElse("Unknown");
            boolean inRoot = file.getParent().equals(this.rootDir);
            String path = inRoot ? file.getFileName().toString() : file.toString().substring(this.rootDir.toString().length());
            return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder().content("").append(((TextComponent)((TextComponent)TextComponent.of("[L]").color(TextColor.GOLD)).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/schem load \"" + path + "\""))).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to load"))))).append((Component)TextComponent.space())).append(((TextComponent)TextComponent.of(path).color(TextColor.DARK_GREEN)).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of(format))))).build();
        }

        @Override
        public int getComponentsSize() {
            return this.files.size();
        }
    }

    private static abstract class SchematicOutputTask<T>
    implements Callable<T> {
        protected final ClipboardFormat format;
        protected final ClipboardHolder holder;

        SchematicOutputTask(ClipboardFormat format, ClipboardHolder holder) {
            this.format = format;
            this.holder = holder;
        }

        protected void writeToOutputStream(OutputStream outputStream) throws IOException, WorldEditException {
            Clipboard clipboard = this.holder.getClipboard();
            Transform transform = this.holder.getTransform();
            Clipboard target = clipboard.transform(transform);
            try (Closer closer = Closer.create();){
                OutputStream stream = closer.register(outputStream);
                BufferedOutputStream bos = closer.register(new BufferedOutputStream(stream));
                ClipboardWriter writer = closer.register(this.format.getWriter(bos));
                writer.write(target);
            }
        }
    }
}

