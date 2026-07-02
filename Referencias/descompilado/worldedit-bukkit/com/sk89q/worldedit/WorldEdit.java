/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit;

import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionBuilder;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxBrushRadiusException;
import com.sk89q.worldedit.MaxRadiusException;
import com.sk89q.worldedit.UnknownDirectionException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.WorldEditManifest;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.platform.BlockInteractEvent;
import com.sk89q.worldedit.event.platform.InputType;
import com.sk89q.worldedit.event.platform.Interaction;
import com.sk89q.worldedit.event.platform.PlayerInputEvent;
import com.sk89q.worldedit.extension.factory.BlockFactory;
import com.sk89q.worldedit.extension.factory.ItemFactory;
import com.sk89q.worldedit.extension.factory.MaskFactory;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extension.platform.PlatformManager;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.SchematicsEventListener;
import com.sk89q.worldedit.internal.expression.invoke.ReturnException;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.scripting.CraftScriptContext;
import com.sk89q.worldedit.scripting.RhinoCraftScriptEngine;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.asset.AssetLoaders;
import com.sk89q.worldedit.util.concurrency.EvenMoreExecutors;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.io.file.FileSelectionAbortedException;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.util.io.file.FilenameResolutionException;
import com.sk89q.worldedit.util.io.file.InvalidFilenameException;
import com.sk89q.worldedit.util.task.SimpleSupervisor;
import com.sk89q.worldedit.util.task.Supervisor;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BundledBlockData;
import com.sk89q.worldedit.world.registry.BundledItemData;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import javax.script.ScriptException;
import org.apache.logging.log4j.Logger;

public final class WorldEdit {
    public static final Logger logger = LogManagerCompat.getLogger();
    private static final WorldEdit instance = new WorldEdit();
    private static String version;
    private final EventBus eventBus = new EventBus();
    private final PlatformManager platformManager = new PlatformManager(this);
    @Deprecated
    private final EditSessionFactory editSessionFactory = new EditSessionFactory.EditSessionFactoryImpl();
    private final SessionManager sessions = new SessionManager(this);
    private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator((ExecutorService)EvenMoreExecutors.newBoundedCachedThreadPool(0, 1, 20, "WorldEdit Task Executor - %s"));
    private final Supervisor supervisor = new SimpleSupervisor();
    private final AssetLoaders assetLoaders = new AssetLoaders(this);
    private final BlockFactory blockFactory = new BlockFactory(this);
    private final ItemFactory itemFactory = new ItemFactory(this);
    private final MaskFactory maskFactory = new MaskFactory(this);
    private final PatternFactory patternFactory = new PatternFactory(this);
    private static final Map<String, Direction> NAME_TO_DIRECTION_MAP;

    private WorldEdit() {
        this.eventBus.register(new SchematicsEventListener());
    }

    public static WorldEdit getInstance() {
        return instance;
    }

    public PlatformManager getPlatformManager() {
        return this.platformManager;
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public Supervisor getSupervisor() {
        return this.supervisor;
    }

    public ListeningExecutorService getExecutorService() {
        return this.executorService;
    }

    public BlockFactory getBlockFactory() {
        return this.blockFactory;
    }

    public ItemFactory getItemFactory() {
        return this.itemFactory;
    }

    public MaskFactory getMaskFactory() {
        return this.maskFactory;
    }

    public PatternFactory getPatternFactory() {
        return this.patternFactory;
    }

    public SessionManager getSessionManager() {
        return this.sessions;
    }

    public TranslationManager getTranslationManager() {
        return this.getPlatformManager().queryCapability(Capability.CONFIGURATION).getTranslationManager();
    }

    public AssetLoaders getAssetLoaders() {
        return this.assetLoaders;
    }

    public File getSafeSaveFile(Actor actor, File dir, String filename, String defaultExt, String ... extensions) throws FilenameException {
        return this.getSafeFile(actor, dir, filename, defaultExt, extensions, true);
    }

    public File getSafeOpenFile(Actor actor, File dir, String filename, String defaultExt, String ... extensions) throws FilenameException {
        return this.getSafeFile(actor, dir, filename, defaultExt, extensions, false);
    }

    private File getSafeFile(@Nullable Actor actor, File dir, String filename, String defaultExt, String[] extensions, boolean isSave) throws FilenameException {
        File f;
        if (extensions != null && extensions.length == 1 && extensions[0] == null) {
            extensions = null;
        }
        if (filename.equals("#") && actor != null) {
            f = isSave ? actor.openFileSaveDialog(extensions) : actor.openFileOpenDialog(extensions);
            if (f == null) {
                throw new FileSelectionAbortedException(TranslatableComponent.of("worldedit.error.no-file-selected"));
            }
        } else {
            Object exts = extensions == null ? ImmutableList.of((Object)defaultExt) : Lists.asList((Object)defaultExt, (Object[])extensions);
            f = this.getSafeFileWithExtensions(dir, filename, (List<String>)exts, isSave);
        }
        try {
            boolean isSym;
            Path filePath = Paths.get(f.toURI()).normalize();
            Path dirPath = Paths.get(dir.toURI()).normalize();
            boolean inDir = filePath.startsWith(dirPath);
            Path existingParent = filePath;
            while ((existingParent = existingParent.getParent()) != null && !existingParent.toFile().exists()) {
            }
            boolean bl = isSym = existingParent != null && !existingParent.toRealPath(new LinkOption[0]).equals(existingParent);
            if (!inDir || !this.getConfiguration().allowSymlinks && isSym) {
                throw new FilenameResolutionException(filename, TranslatableComponent.of("worldedit.error.file-resolution.outside-root"));
            }
            return filePath.toFile();
        }
        catch (IOException | InvalidPathException e) {
            throw new FilenameResolutionException(filename, TranslatableComponent.of("worldedit.error.file-resolution.resolve-failed"));
        }
    }

    private File getSafeFileWithExtensions(File dir, String filename, List<String> exts, boolean isSave) throws InvalidFilenameException {
        if (isSave) {
            if (exts.size() != 1) {
                exts = exts.subList(0, 1);
            }
        } else {
            File f;
            String currentExt;
            int dot = filename.lastIndexOf(46);
            if ((dot < 0 || dot == filename.length() - 1) && exts.contains(currentExt = filename.substring(dot + 1)) && this.checkFilename(filename) && (f = new File(dir, filename)).exists()) {
                return f;
            }
        }
        File result = null;
        Iterator<String> iter = exts.iterator();
        while (iter.hasNext() && (result == null || !isSave && !result.exists())) {
            result = this.getSafeFileWithExtension(dir, filename, iter.next());
        }
        if (result == null) {
            throw new InvalidFilenameException(filename, TranslatableComponent.of("worldedit.error.invalid-filename.invalid-characters"));
        }
        return result;
    }

    private File getSafeFileWithExtension(File dir, String filename, String extension) {
        int dot;
        if (!(extension == null || (dot = ((String)filename).lastIndexOf(46)) >= 0 && dot != ((String)filename).length() - 1 && ((String)filename).substring(dot + 1).equalsIgnoreCase(extension))) {
            filename = (String)filename + "." + extension;
        }
        if (!this.checkFilename((String)filename)) {
            return null;
        }
        return new File(dir, (String)filename);
    }

    private boolean checkFilename(String filename) {
        return filename.matches("^[A-Za-z0-9_\\- \\./\\\\'\\$@~!%\\^\\*\\(\\)\\[\\]\\+\\{\\},\\?]+\\.[A-Za-z0-9]+$");
    }

    public void loadMappings() {
        BundledBlockData.getInstance();
        BundledItemData.getInstance();
        LegacyMapper.getInstance();
    }

    public void checkMaxRadius(double radius) throws MaxRadiusException {
        if (this.getConfiguration().maxRadius > 0 && radius > (double)this.getConfiguration().maxRadius) {
            throw new MaxRadiusException();
        }
    }

    public void checkMaxBrushRadius(double radius) throws MaxBrushRadiusException {
        if (this.getConfiguration().maxBrushRadius > 0 && radius > (double)this.getConfiguration().maxBrushRadius) {
            throw new MaxBrushRadiusException();
        }
    }

    @Deprecated
    public File getWorkingDirectoryFile(String path) {
        return this.getWorkingDirectoryPath(path).toFile();
    }

    public Path getWorkingDirectoryPath(String path) {
        Path p = Paths.get(path, new String[0]);
        if (p.isAbsolute()) {
            return p;
        }
        return this.getConfiguration().getWorkingDirectoryPath().resolve(path);
    }

    public BlockVector3 getDirection(@Nullable Player player, String dirStr) throws UnknownDirectionException {
        Direction dir = this.getPlayerDirection(player, dirStr = dirStr.toLowerCase(Locale.ROOT));
        if (dir.isUpright() || dir.isCardinal()) {
            return dir.toBlockVector();
        }
        throw new UnknownDirectionException(dir.name());
    }

    public BlockVector3 getDiagonalDirection(@Nullable Player player, String dirStr) throws UnknownDirectionException {
        Direction dir = this.getPlayerDirection(player, dirStr = dirStr.toLowerCase(Locale.ROOT));
        if (dir.isCardinal() || dir.isOrdinal() || dir.isUpright()) {
            return dir.toBlockVector();
        }
        throw new UnknownDirectionException(dir.name());
    }

    private Direction getPlayerDirection(@Nullable Player player, String dirStr) throws UnknownDirectionException {
        Direction byName = NAME_TO_DIRECTION_MAP.get(dirStr);
        if (byName != null) {
            return byName;
        }
        switch (dirStr) {
            case "m": 
            case "me": 
            case "f": 
            case "forward": {
                return this.getDirectionRelative(player, 0);
            }
            case "b": 
            case "back": {
                Direction dir = this.getDirectionRelative(player, 180);
                if (dir.isUpright()) {
                    dir = dir == Direction.UP ? Direction.DOWN : Direction.UP;
                }
                return dir;
            }
            case "l": 
            case "left": {
                return this.getDirectionRelative(player, -90);
            }
            case "r": 
            case "right": {
                return this.getDirectionRelative(player, 90);
            }
        }
        throw new UnknownDirectionException(dirStr);
    }

    private Direction getDirectionRelative(Player player, int yawOffset) throws UnknownDirectionException {
        if (player != null) {
            return player.getCardinalDirection(yawOffset);
        }
        throw new UnknownDirectionException("Only a player can use relative directions");
    }

    public void flushBlockBag(Actor actor, EditSession editSession) {
        Map<BlockType, Integer> missingBlocks;
        BlockBag blockBag = editSession.getBlockBag();
        if (blockBag != null) {
            blockBag.flushChanges();
        }
        if (!(missingBlocks = editSession.popMissingBlocks()).isEmpty()) {
            TextComponent.Builder str = TextComponent.builder();
            str.append("Missing these blocks: ");
            int size = missingBlocks.size();
            int i = 0;
            for (Map.Entry<BlockType, Integer> blockTypeIntegerEntry : missingBlocks.entrySet()) {
                str.append(blockTypeIntegerEntry.getKey().getRichName());
                ((TextComponent.Builder)((TextComponent.Builder)str.append(" [Amt: ")).append(String.valueOf(blockTypeIntegerEntry.getValue()))).append("]");
                if (++i == size) continue;
                str.append(", ");
            }
            actor.printError((Component)str.build());
        }
    }

    public boolean handleArmSwing(Player player) {
        PlayerInputEvent event = new PlayerInputEvent(player, InputType.PRIMARY);
        this.getEventBus().post(event);
        return event.isCancelled();
    }

    public boolean handleRightClick(Player player) {
        PlayerInputEvent event = new PlayerInputEvent(player, InputType.SECONDARY);
        this.getEventBus().post(event);
        return event.isCancelled();
    }

    @Deprecated
    public boolean handleBlockRightClick(Player player, Location clicked) {
        return this.handleBlockRightClick(player, clicked, null);
    }

    public boolean handleBlockRightClick(Player player, Location clicked, @Nullable Direction face) {
        BlockInteractEvent event = new BlockInteractEvent(player, clicked, face, Interaction.OPEN);
        this.getEventBus().post(event);
        return event.isCancelled();
    }

    @Deprecated
    public boolean handleBlockLeftClick(Player player, Location clicked) {
        return this.handleBlockLeftClick(player, clicked, null);
    }

    public boolean handleBlockLeftClick(Player player, Location clicked, @Nullable Direction face) {
        BlockInteractEvent event = new BlockInteractEvent(player, clicked, face, Interaction.HIT);
        this.getEventBus().post(event);
        return event.isCancelled();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runScript(Player player, File f, String[] args) throws WorldEditException {
        RhinoCraftScriptEngine engine;
        String script;
        int index;
        String filename = f.getPath();
        String ext = filename.substring((index = filename.lastIndexOf(46)) + 1);
        if (!ext.equalsIgnoreCase("js")) {
            player.printError(TranslatableComponent.of("worldedit.script.unsupported"));
            return;
        }
        try {
            InputStream file;
            if (!f.exists()) {
                file = WorldEdit.class.getResourceAsStream("craftscripts/" + filename);
                if (file == null) {
                    player.printError(TranslatableComponent.of("worldedit.script.file-not-found", TextComponent.of(filename)));
                    return;
                }
            } else {
                file = new FileInputStream(f);
            }
            DataInputStream in = new DataInputStream(file);
            byte[] data = new byte[in.available()];
            in.readFully(data);
            in.close();
            script = new String(data, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            player.printError(TranslatableComponent.of("worldedit.script.read-error", TextComponent.of(e.getMessage())));
            return;
        }
        LocalSession session = this.getSessionManager().get(player);
        CraftScriptContext scriptContext = new CraftScriptContext(this, this.getPlatformManager().queryCapability(Capability.USER_COMMANDS), this.getConfiguration(), session, player, args);
        try {
            engine = new RhinoCraftScriptEngine();
        }
        catch (NoClassDefFoundError ignored) {
            player.printError(TranslatableComponent.of("worldedit.script.no-script-engine"));
            return;
        }
        engine.setTimeLimit(this.getConfiguration().scriptTimeout);
        HashMap<String, Object> vars = new HashMap<String, Object>();
        vars.put("argv", args);
        vars.put("context", scriptContext);
        vars.put("player", player);
        try {
            engine.evaluate(script, filename, vars);
        }
        catch (ScriptException e) {
            if (!(Throwables.getRootCause((Throwable)e) instanceof ReturnException)) {
                player.printError(TranslatableComponent.of("worldedit.script.failed", TextComponent.of(e.getMessage(), TextColor.WHITE)));
                logger.warn("Failed to execute script", (Throwable)e);
            }
        }
        catch (WorldEditException | NumberFormatException e) {
            throw e;
        }
        catch (Throwable e) {
            player.printError(TranslatableComponent.of("worldedit.script.failed-console", TextComponent.of(e.getClass().getCanonicalName(), TextColor.WHITE)));
            logger.warn("Failed to execute script", e);
        }
        finally {
            for (EditSession editSession : scriptContext.getEditSessions()) {
                editSession.close();
                session.remember(editSession);
            }
        }
    }

    public LocalConfiguration getConfiguration() {
        return this.getPlatformManager().getConfiguration();
    }

    @Deprecated
    public EditSessionFactory getEditSessionFactory() {
        return this.editSessionFactory;
    }

    public EditSessionBuilder newEditSessionBuilder() {
        return new EditSessionBuilder(this.eventBus);
    }

    public EditSession newEditSession(@Nullable World world) {
        return this.newEditSessionBuilder().world(world).build();
    }

    public <A extends Actor & Locatable> EditSession newEditSession(A locatableActor) {
        return this.newEditSessionBuilder().locatableActor(locatableActor).build();
    }

    public static String getVersion() {
        if (version != null) {
            return version;
        }
        WorldEditManifest manifest = WorldEditManifest.load();
        version = manifest.getWorldEditVersion();
        return version;
    }

    static {
        WorldEdit.getVersion();
        HashMultimap directionNames = HashMultimap.create();
        for (Direction direction : Direction.valuesOf(Direction.Flag.CARDINAL | Direction.Flag.UPRIGHT)) {
            String name2 = direction.name().toLowerCase(Locale.ROOT);
            for (int i = 1; i <= name2.length(); ++i) {
                directionNames.put((Object)direction, (Object)name2.substring(0, i));
            }
        }
        ImmutableMap.Builder nameToDirectionMap = ImmutableMap.builder();
        for (Direction direction : directionNames.keySet()) {
            directionNames.get((Object)direction).forEach(name -> nameToDirectionMap.put(name, (Object)direction));
        }
        for (Direction direction : ImmutableList.of((Object)((Object)Direction.NORTH), (Object)((Object)Direction.SOUTH))) {
            for (Direction diagonal : ImmutableList.of((Object)((Object)Direction.WEST), (Object)((Object)Direction.EAST))) {
                for (String dirName : directionNames.get((Object)direction)) {
                    for (String diagName : directionNames.get((Object)diagonal)) {
                        nameToDirectionMap.put((Object)(dirName + diagName), (Object)Direction.valueOf(direction.name() + diagonal.name()));
                    }
                }
            }
        }
        NAME_TO_DIRECTION_MAP = nameToDirectionMap.build();
    }
}

