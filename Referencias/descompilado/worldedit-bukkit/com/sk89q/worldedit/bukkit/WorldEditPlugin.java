/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.server.AsyncTabCompleteEvent
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Registry
 *  org.bukkit.Tag
 *  org.bukkit.command.BlockCommandSender
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.WorldInitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.plugin.messaging.PluginMessageListener
 */
package com.sk89q.worldedit.bukkit;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.bukkit.util.ClassSourceValidator;
import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bstats.bukkit.Metrics;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitBlockCommandSender;
import com.sk89q.worldedit.bukkit.BukkitCommandSender;
import com.sk89q.worldedit.bukkit.BukkitConfiguration;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitServerInterface;
import com.sk89q.worldedit.bukkit.CUIChannelListener;
import com.sk89q.worldedit.bukkit.WorldEditListener;
import com.sk89q.worldedit.bukkit.adapter.AdapterLoadException;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplLoader;
import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.event.platform.CommandSuggestionEvent;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import com.sk89q.worldedit.event.platform.PlatformUnreadyEvent;
import com.sk89q.worldedit.event.platform.PlatformsRegisteredEvent;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.anvil.ChunkDeleter;
import com.sk89q.worldedit.internal.command.CommandUtil;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.lifecycle.Lifecycled;
import com.sk89q.worldedit.util.lifecycle.SimpleLifecycled;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockCategory;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.FuzzyBlockState;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldedit.world.item.ItemCategory;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.weather.WeatherTypes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.enginehub.piston.CommandManager;

public class WorldEditPlugin
extends JavaPlugin
implements TabCompleter {
    private static final String FAILED_VERSION_CHECK = "\n**********************************************\n** This Minecraft version (%s) is not supported by this version of WorldEdit.\n** Please download an OLDER version of WorldEdit which does.\n**********************************************\n";
    private static final Logger LOGGER;
    public static final String CUI_PLUGIN_CHANNEL = "worldedit:cui";
    private static WorldEditPlugin INSTANCE;
    private static final int BSTATS_PLUGIN_ID = 3328;
    private final SimpleLifecycled<BukkitImplAdapter> adapter = SimpleLifecycled.invalid();
    private BukkitServerInterface platform;
    private BukkitConfiguration config;

    public void onLoad() {
        INSTANCE = this;
        this.getDataFolder().mkdirs();
        WorldEdit worldEdit = WorldEdit.getInstance();
        this.platform = new BukkitServerInterface(this, this.getServer());
        worldEdit.getPlatformManager().register(this.platform);
        this.createDefaultConfiguration("config.yml");
        this.config = new BukkitConfiguration(new YAMLProcessor(new File(this.getDataFolder(), "config.yml"), true), this);
        Path delChunks = Paths.get(this.getDataFolder().getPath(), "delete_chunks.json");
        if (Files.exists(delChunks, new LinkOption[0])) {
            ChunkDeleter.runFromFile(delChunks, true);
        }
    }

    public void onEnable() {
        ClassSourceValidator verifier = new ClassSourceValidator((Plugin)this);
        verifier.reportMismatches((List<Class<?>>)ImmutableList.of(World.class, CommandManager.class, EditSession.class, Actor.class));
        WorldEdit.getInstance().getEventBus().post(new PlatformsRegisteredEvent());
        PermissionsResolverManager.initialize((Plugin)this);
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, CUI_PLUGIN_CHANNEL, (PluginMessageListener)new CUIChannelListener(this));
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, CUI_PLUGIN_CHANNEL);
        this.getServer().getPluginManager().registerEvents((Listener)new WorldEditListener(this), (Plugin)this);
        if (PaperLib.isPaper()) {
            this.getServer().getPluginManager().registerEvents((Listener)new AsyncTabCompleteListener(), (Plugin)this);
        }
        if (Bukkit.getWorlds().isEmpty()) {
            this.setupPreWorldData();
            this.getServer().getPluginManager().registerEvents((Listener)new WorldInitListener(), (Plugin)this);
        } else {
            this.getLogger().warning("Server reload detected. This may cause various issues with WorldEdit and dependent plugins.");
            try {
                this.setupPreWorldData();
                this.setupWorldData();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        new Metrics(this, 3328);
        PaperLib.suggestPaper((Plugin)this);
    }

    private void setupPreWorldData() {
        this.loadAdapter();
        this.initializeRegistries();
        this.config.load();
        WorldEdit.getInstance().loadMappings();
    }

    private void setupWorldData() {
        this.setupTags();
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent(this.platform));
    }

    private void initializeRegistries() {
        Registry.BIOME.forEach(biome -> {
            if (!biome.name().equals("CUSTOM")) {
                String key = biome.getKey().toString();
                BiomeType.REGISTRY.register(key, new BiomeType(key));
            }
        });
        Registry.MATERIAL.forEach(material -> {
            String key = material.getKey().toString();
            if (material.isBlock()) {
                BlockType.REGISTRY.register(key, new BlockType(key, blockState -> {
                    ParserContext context = new ParserContext();
                    context.setPreferringWildcard(true);
                    context.setTryLegacy(false);
                    context.setRestricted(false);
                    try {
                        FuzzyBlockState state = (FuzzyBlockState)((BaseBlock)WorldEdit.getInstance().getBlockFactory().parseFromInput(BukkitAdapter.adapt(blockState.getBlockType()).createBlockData().getAsString(), context)).toImmutableState();
                        BlockStateHolder<BlockState> defaultState = blockState.getBlockType().getAllStates().get(0);
                        for (Map.Entry<Property<?>, Object> propertyObjectEntry : state.getStates().entrySet()) {
                            defaultState = defaultState.with(propertyObjectEntry.getKey(), propertyObjectEntry.getValue());
                        }
                        return defaultState;
                    }
                    catch (InputParseException e) {
                        this.getLogger().log(Level.WARNING, "Error loading block state for " + key, e);
                        return blockState;
                    }
                }));
            }
            if (material.isItem()) {
                ItemType.REGISTRY.register(key, new ItemType(key));
            }
        });
        Registry.ENTITY_TYPE.forEach(entityType -> {
            String key = entityType.getKey().toString();
            EntityType.REGISTRY.register(key, new EntityType(key));
        });
        GameModes.get("");
        WeatherTypes.get("");
        BukkitImplAdapter adapter = this.getBukkitImplAdapter();
        if (adapter != null) {
            adapter.initializeRegistries();
        }
    }

    private void setupTags() {
        String key;
        for (Tag blockTag : Bukkit.getTags((String)"blocks", Material.class)) {
            key = blockTag.getKey().toString();
            BlockCategory.REGISTRY.register(key, new BlockCategory(key));
        }
        for (Tag itemTag : Bukkit.getTags((String)"items", Material.class)) {
            key = itemTag.getKey().toString();
            ItemCategory.REGISTRY.register(key, new ItemCategory(key));
        }
    }

    private void loadAdapter() {
        WorldEdit worldEdit = WorldEdit.getInstance();
        BukkitImplLoader adapterLoader = new BukkitImplLoader();
        try {
            adapterLoader.addFromPath(((Object)((Object)this)).getClass().getClassLoader());
        }
        catch (IOException e) {
            LOGGER.warn("Failed to search path for Bukkit adapters");
        }
        try {
            adapterLoader.addFromJar(this.getFile());
        }
        catch (IOException e) {
            LOGGER.warn("Failed to search " + String.valueOf(this.getFile()) + " for Bukkit adapters", (Throwable)e);
        }
        try {
            BukkitImplAdapter bukkitAdapter = adapterLoader.loadAdapter();
            LOGGER.info("Using " + bukkitAdapter.getClass().getCanonicalName() + " as the Bukkit adapter");
            this.adapter.newValue(bukkitAdapter);
        }
        catch (AdapterLoadException e) {
            Platform platform = worldEdit.getPlatformManager().queryCapability(Capability.WORLD_EDITING);
            if (platform instanceof BukkitServerInterface) {
                LOGGER.warn(e.getMessage());
                try {
                    Class.forName("org.spigotmc.SpigotConfig");
                }
                catch (ClassNotFoundException e1) {
                    LOGGER.warn("CraftBukkit is not a supported Bukkit platform. Please use a supported platform, such as Paper.");
                }
            } else {
                LOGGER.info("WorldEdit could not find a Bukkit adapter for this MC version, but it seems that you have another implementation of WorldEdit installed (" + platform.getPlatformName() + ") that handles the world editing.");
            }
            this.adapter.invalidate();
        }
    }

    public void onDisable() {
        WorldEdit worldEdit = WorldEdit.getInstance();
        worldEdit.getSessionManager().unload();
        if (this.platform != null) {
            worldEdit.getEventBus().post(new PlatformUnreadyEvent(this.platform));
            worldEdit.getPlatformManager().unregister(this.platform);
            this.platform.unregisterCommands();
        }
        if (this.config != null) {
            this.config.unload();
        }
        this.getServer().getScheduler().cancelTasks((Plugin)this);
    }

    protected void loadConfiguration() {
        this.config.unload();
        this.config.load();
        this.getPermissionsResolver().load();
    }

    protected void createDefaultConfiguration(String name) {
        File actual = new File(this.getDataFolder(), name);
        if (!actual.exists()) {
            try (InputStream stream = this.getResource("defaults/" + name);){
                if (stream == null) {
                    throw new FileNotFoundException();
                }
                this.copyDefaultConfig(stream, actual, name);
            }
            catch (IOException e) {
                this.getLogger().severe("Unable to read default configuration: " + name);
            }
        }
    }

    private void copyDefaultConfig(InputStream input, File actual, String name) {
        try (FileOutputStream output = new FileOutputStream(actual);){
            int length;
            byte[] buf = new byte[8192];
            while ((length = input.read(buf)) > 0) {
                output.write(buf, 0, length);
            }
            this.getLogger().info("Default configuration file written: " + name);
        }
        catch (IOException e) {
            this.getLogger().log(Level.WARNING, "Failed to write default config file", e);
        }
    }

    private String rebuildArguments(String commandLabel, String[] args) {
        int plSep = commandLabel.indexOf(58);
        if (plSep >= 0 && plSep < commandLabel.length() + 1) {
            commandLabel = commandLabel.substring(plSep + 1);
        }
        StringBuilder sb = new StringBuilder("/").append(commandLabel);
        if (args.length > 0) {
            sb.append(" ");
        }
        return Joiner.on((String)" ").appendTo(sb, (Object[])args).toString();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String arguments = this.rebuildArguments(commandLabel, args);
        CommandEvent event = new CommandEvent(this.wrapCommandSender(sender), arguments);
        this.getWorldEdit().getEventBus().post(event);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String arguments = this.rebuildArguments(commandLabel, args);
        CommandSuggestionEvent event = new CommandSuggestionEvent(this.wrapCommandSender(sender), arguments);
        this.getWorldEdit().getEventBus().post(event);
        return CommandUtil.fixSuggestions(arguments, event.getSuggestions());
    }

    public LocalSession getSession(Player player) {
        return WorldEdit.getInstance().getSessionManager().get(this.wrapPlayer(player));
    }

    public EditSession createEditSession(Player player) {
        BukkitPlayer wePlayer = this.wrapPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(wePlayer);
        BlockBag blockBag = session.getBlockBag(wePlayer);
        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().locatableActor(wePlayer).maxBlocks(session.getBlockChangeLimit()).blockBag(blockBag).build();
        editSession.enableStandardMode();
        return editSession;
    }

    public void remember(Player player, EditSession editSession) {
        BukkitPlayer wePlayer = this.wrapPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(wePlayer);
        session.remember(editSession);
        editSession.close();
        WorldEdit.getInstance().flushBlockBag(wePlayer, editSession);
    }

    public BukkitConfiguration getLocalConfiguration() {
        return this.config;
    }

    public PermissionsResolverManager getPermissionsResolver() {
        return PermissionsResolverManager.getInstance();
    }

    public BukkitPlayer wrapPlayer(Player player) {
        return new BukkitPlayer(this, player);
    }

    public Actor wrapCommandSender(CommandSender sender) {
        if (sender instanceof Player) {
            return this.wrapPlayer((Player)sender);
        }
        if (this.config.commandBlockSupport && sender instanceof BlockCommandSender) {
            return new BukkitBlockCommandSender(this, (BlockCommandSender)sender);
        }
        return new BukkitCommandSender(this, sender);
    }

    BukkitServerInterface getInternalPlatform() {
        return this.platform;
    }

    public WorldEdit getWorldEdit() {
        return WorldEdit.getInstance();
    }

    static WorldEditPlugin getInstance() {
        return (WorldEditPlugin)((Object)Preconditions.checkNotNull((Object)((Object)INSTANCE)));
    }

    Lifecycled<BukkitImplAdapter> getLifecycledBukkitImplAdapter() {
        return this.adapter;
    }

    BukkitImplAdapter getBukkitImplAdapter() {
        return this.adapter.value().orElse(null);
    }

    static {
        if (PaperLib.getMinecraftVersion() < 13) {
            throw new IllegalStateException(String.format(FAILED_VERSION_CHECK, Bukkit.getVersion()));
        }
        LOGGER = LogManagerCompat.getLogger();
    }

    private class AsyncTabCompleteListener
    implements Listener {
        AsyncTabCompleteListener() {
        }

        @EventHandler(ignoreCancelled=true)
        public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
            Optional<org.enginehub.piston.Command> command;
            if (!event.isCommand()) {
                return;
            }
            Object buffer = event.getBuffer();
            int firstSpace = ((String)buffer).indexOf(32);
            if (firstSpace < 1) {
                return;
            }
            String label = ((String)buffer).substring(1, firstSpace);
            Plugin owner = WorldEditPlugin.this.platform.getDynamicCommands().getCommandOwner(label);
            if (owner != WorldEditPlugin.this) {
                return;
            }
            int plSep = label.indexOf(58);
            if (plSep >= 0 && plSep < label.length() + 1) {
                label = label.substring(plSep + 1);
                buffer = "/" + ((String)buffer).substring(plSep + 2);
            }
            if (!(command = WorldEdit.getInstance().getPlatformManager().getPlatformCommandManager().getCommandManager().getCommand(label)).isPresent()) {
                return;
            }
            CommandSuggestionEvent suggestEvent = new CommandSuggestionEvent(WorldEditPlugin.this.wrapCommandSender(event.getSender()), (String)buffer);
            WorldEditPlugin.this.getWorldEdit().getEventBus().post(suggestEvent);
            event.setCompletions(CommandUtil.fixSuggestions((String)buffer, suggestEvent.getSuggestions()));
            event.setHandled(true);
        }
    }

    private class WorldInitListener
    implements Listener {
        private boolean loaded = false;

        private WorldInitListener() {
        }

        @EventHandler(priority=EventPriority.LOWEST)
        public void onWorldInit(WorldInitEvent event) {
            if (this.loaded) {
                return;
            }
            this.loaded = true;
            WorldEditPlugin.this.setupWorldData();
        }
    }
}

