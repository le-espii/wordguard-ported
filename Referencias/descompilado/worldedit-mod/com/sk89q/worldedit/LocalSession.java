/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionBuilder;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.tool.BlockTool;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.NavigationWand;
import com.sk89q.worldedit.command.tool.SelectionWand;
import com.sk89q.worldedit.command.tool.SinglePickaxe;
import com.sk89q.worldedit.command.tool.Tool;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.internal.cui.CUIRegion;
import com.sk89q.worldedit.internal.cui.SelectionShapeEvent;
import com.sk89q.worldedit.internal.cui.ServerCUIHandler;
import com.sk89q.worldedit.jchronic.Chronic;
import com.sk89q.worldedit.jchronic.Options;
import com.sk89q.worldedit.jchronic.utils.Span;
import com.sk89q.worldedit.jchronic.utils.Time;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.RegionSelectorType;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.Placement;
import com.sk89q.worldedit.session.PlacementType;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldedit.world.snapshot.experimental.Snapshot;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinTagType;

public class LocalSession {
    private static final transient int CUI_VERSION_UNINITIALIZED = -1;
    public static transient int MAX_HISTORY_SIZE = 15;
    private transient LocalConfiguration config;
    private final transient AtomicBoolean dirty = new AtomicBoolean();
    private transient int failedCuiAttempts = 0;
    private transient boolean hasCUISupport = false;
    private transient int cuiVersion = -1;
    private transient RegionSelector selector = new CuboidRegionSelector();
    private transient Placement placement = new Placement(PlacementType.PLAYER, BlockVector3.ZERO);
    private final transient LinkedList<EditSession> history = new LinkedList();
    private transient int historyPointer = 0;
    private transient ClipboardHolder clipboard;
    private transient boolean superPickaxe = false;
    private transient BlockTool pickaxeMode = new SinglePickaxe();
    private final transient Map<ItemType, Tool> tools = new HashMap<ItemType, Tool>();
    private transient int maxBlocksChanged = -1;
    private transient int maxTimeoutTime;
    private transient boolean useInventory;
    private transient com.sk89q.worldedit.world.snapshot.Snapshot snapshot;
    private transient Snapshot snapshotExperimental;
    private transient SideEffectSet sideEffectSet = SideEffectSet.defaults();
    private transient Mask mask;
    private transient ZoneId timezone = ZoneId.systemDefault();
    private transient BlockVector3 cuiTemporaryBlock;
    private transient EditSession.ReorderMode reorderMode = EditSession.ReorderMode.FAST;
    private transient List<Countable<BlockState>> lastDistribution;
    private transient World worldOverride;
    private transient boolean tickingWatchdog = true;
    private transient boolean hasBeenToldVersion;
    private transient boolean tracingActions;
    private String lastScript;
    private RegionSelectorType defaultSelector;
    private boolean useServerCUI = false;
    private String wandItem;
    private Boolean wandItemDefault;
    private String navWandItem;
    private Boolean navWandItemDefault;

    public LocalSession() {
    }

    public LocalSession(@Nullable LocalConfiguration config) {
        this.config = config;
    }

    public void setConfiguration(LocalConfiguration config) {
        Preconditions.checkNotNull((Object)config);
        this.config = config;
    }

    public void postLoad() {
        if (this.defaultSelector != null) {
            this.selector = this.defaultSelector.createSelector();
        }
    }

    public boolean isDirty() {
        return this.dirty.get();
    }

    private void setDirty() {
        this.dirty.set(true);
    }

    public boolean compareAndResetDirty() {
        return this.dirty.compareAndSet(true, false);
    }

    public ZoneId getTimeZone() {
        return this.timezone;
    }

    public void setTimezone(ZoneId timezone) {
        Preconditions.checkNotNull((Object)timezone);
        this.timezone = timezone;
    }

    public void clearHistory() {
        this.history.clear();
        this.historyPointer = 0;
    }

    public void remember(EditSession editSession) {
        Preconditions.checkNotNull((Object)editSession);
        if (editSession.size() == 0) {
            return;
        }
        while (this.historyPointer < this.history.size()) {
            this.history.remove(this.historyPointer);
        }
        this.history.add(editSession);
        while (this.history.size() > MAX_HISTORY_SIZE) {
            this.history.remove(0);
        }
        this.historyPointer = this.history.size();
    }

    public EditSession undo(@Nullable BlockBag newBlockBag, Actor actor) {
        Preconditions.checkNotNull((Object)actor);
        --this.historyPointer;
        if (this.historyPointer >= 0) {
            EditSession editSession = this.history.get(this.historyPointer);
            try (EditSession newEditSession = WorldEdit.getInstance().newEditSessionBuilder().world(editSession.getWorld()).blockBag(newBlockBag).actor(actor).build();){
                this.prepareEditingExtents(newEditSession, actor);
                editSession.undo(newEditSession);
            }
            return editSession;
        }
        this.historyPointer = 0;
        return null;
    }

    public EditSession redo(@Nullable BlockBag newBlockBag, Actor actor) {
        Preconditions.checkNotNull((Object)actor);
        if (this.historyPointer < this.history.size()) {
            EditSession editSession = this.history.get(this.historyPointer);
            try (EditSession newEditSession = WorldEdit.getInstance().newEditSessionBuilder().world(editSession.getWorld()).blockBag(newBlockBag).actor(actor).build();){
                this.prepareEditingExtents(newEditSession, actor);
                editSession.redo(newEditSession);
            }
            ++this.historyPointer;
            return editSession;
        }
        return null;
    }

    public boolean hasWorldOverride() {
        return this.worldOverride != null;
    }

    @Nullable
    public World getWorldOverride() {
        return this.worldOverride;
    }

    public void setWorldOverride(@Nullable World worldOverride) {
        this.worldOverride = worldOverride;
    }

    public boolean isTickingWatchdog() {
        return this.tickingWatchdog;
    }

    public void setTickingWatchdog(boolean tickingWatchdog) {
        this.tickingWatchdog = tickingWatchdog;
    }

    public boolean isTracingActions() {
        return this.tracingActions;
    }

    public void setTracingActions(boolean tracingActions) {
        this.tracingActions = tracingActions;
    }

    public RegionSelectorType getDefaultRegionSelector() {
        return this.defaultSelector;
    }

    public void setDefaultRegionSelector(RegionSelectorType defaultSelector) {
        Preconditions.checkNotNull((Object)((Object)defaultSelector));
        this.defaultSelector = defaultSelector;
        this.setDirty();
    }

    public RegionSelector getRegionSelector(World world) {
        Preconditions.checkNotNull((Object)world);
        if (this.selector.getWorld() == null || !this.selector.getWorld().equals(world)) {
            this.selector.setWorld(world);
            this.selector.clear();
            if (this.hasWorldOverride() && !world.equals(this.getWorldOverride())) {
                this.setWorldOverride(null);
            }
        }
        return this.selector;
    }

    public void setRegionSelector(World world, RegionSelector selector) {
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull((Object)selector);
        selector.setWorld(world);
        this.selector = selector;
        if (this.hasWorldOverride() && !world.equals(this.getWorldOverride())) {
            this.setWorldOverride(null);
        }
    }

    public boolean isSelectionDefined(World world) {
        Preconditions.checkNotNull((Object)world);
        if (this.selector.getIncompleteRegion().getWorld() == null || !this.selector.getIncompleteRegion().getWorld().equals(world)) {
            return false;
        }
        return this.selector.isDefined();
    }

    public Region getSelection() throws IncompleteRegionException {
        return this.getSelection(this.getSelectionWorld());
    }

    public Region getSelection(@Nullable World world) throws IncompleteRegionException {
        if (world == null || this.selector.getIncompleteRegion().getWorld() == null || !this.selector.getIncompleteRegion().getWorld().equals(world)) {
            throw new IncompleteRegionException();
        }
        return this.selector.getRegion();
    }

    @Nullable
    public World getSelectionWorld() {
        return this.selector.getIncompleteRegion().getWorld();
    }

    public ClipboardHolder getClipboard() throws EmptyClipboardException {
        if (this.clipboard == null) {
            throw new EmptyClipboardException();
        }
        return this.clipboard;
    }

    public void setClipboard(@Nullable ClipboardHolder clipboard) {
        this.clipboard = clipboard;
    }

    @Deprecated
    public boolean isToolControlEnabled() {
        return true;
    }

    @Deprecated
    public void setToolControl(boolean toolControl) {
    }

    public int getBlockChangeLimit() {
        return this.maxBlocksChanged;
    }

    public void setBlockChangeLimit(int maxBlocksChanged) {
        this.maxBlocksChanged = maxBlocksChanged;
    }

    public int getTimeout() {
        return this.maxTimeoutTime;
    }

    public void setTimeout(int timeout) {
        this.maxTimeoutTime = timeout;
    }

    public boolean hasSuperPickAxe() {
        return this.superPickaxe;
    }

    public void enableSuperPickAxe() {
        this.superPickaxe = true;
    }

    public void disableSuperPickAxe() {
        this.superPickaxe = false;
    }

    public boolean toggleSuperPickAxe() {
        this.superPickaxe = !this.superPickaxe;
        return this.superPickaxe;
    }

    public BlockVector3 getPlacementPosition(Actor actor) throws IncompleteRegionException {
        return this.placement.getPlacementPosition(this.selector, actor);
    }

    public Placement getPlacement() {
        return this.placement;
    }

    public void setPlacement(Placement placement) {
        if (placement.placementType() == PlacementType.HERE) {
            throw new IllegalStateException("PlacementType.HERE cannot be used. Use PLAYER or WORLD instead.");
        }
        this.placement = placement;
    }

    @Deprecated
    public void setPlaceAtPos1(boolean placeAtPos1) {
        this.placement = new Placement(placeAtPos1 ? PlacementType.POS1 : PlacementType.PLAYER, BlockVector3.ZERO);
    }

    @Deprecated
    public boolean isPlaceAtPos1() {
        return this.placement.placementType() == PlacementType.POS1;
    }

    @Deprecated
    public boolean togglePlacementPosition() {
        this.placement = this.placement.placementType() == PlacementType.POS1 ? new Placement(PlacementType.PLAYER, BlockVector3.ZERO) : new Placement(PlacementType.POS1, BlockVector3.ZERO);
        return this.placement.placementType() == PlacementType.POS1;
    }

    @Nullable
    public BlockBag getBlockBag(Player player) {
        Preconditions.checkNotNull((Object)player);
        if (!this.useInventory) {
            return null;
        }
        return player.getInventoryBlockBag();
    }

    @Nullable
    public com.sk89q.worldedit.world.snapshot.Snapshot getSnapshot() {
        return this.snapshot;
    }

    public void setSnapshot(@Nullable com.sk89q.worldedit.world.snapshot.Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Nullable
    public Snapshot getSnapshotExperimental() {
        return this.snapshotExperimental;
    }

    public void setSnapshotExperimental(@Nullable Snapshot snapshotExperimental) {
        this.snapshotExperimental = snapshotExperimental;
    }

    public BlockTool getSuperPickaxe() {
        return this.pickaxeMode;
    }

    public void setSuperPickaxe(BlockTool tool) {
        Preconditions.checkNotNull((Object)tool);
        this.pickaxeMode = tool;
    }

    @Nullable
    public Tool getTool(ItemType item) {
        return this.tools.get(item);
    }

    @Deprecated
    public BrushTool getBrushTool(ItemType item) throws InvalidToolBindException {
        Tool tool = this.getTool(item);
        if (!(tool instanceof BrushTool)) {
            tool = new BrushTool("worldedit.brush.sphere");
            this.setTool(item, tool);
        }
        return (BrushTool)tool;
    }

    @Nullable
    public BrushTool getBrush(ItemType item) {
        BrushTool tool;
        Tool tool2 = this.getTool(item);
        return tool2 instanceof BrushTool ? (tool = (BrushTool)tool2) : null;
    }

    public BrushTool forceBrush(ItemType item, Brush brush, String permission) throws InvalidToolBindException {
        BrushTool tool = this.getBrush(item);
        if (tool == null) {
            tool = new BrushTool(brush, permission);
            this.setTool(item, tool);
        } else {
            tool.setBrush(brush, permission);
        }
        return tool;
    }

    public void setTool(ItemType item, @Nullable Tool tool) throws InvalidToolBindException {
        if (item.hasBlockType()) {
            throw new InvalidToolBindException(item, TranslatableComponent.of("worldedit.tool.error.item-only"));
        }
        if (tool instanceof SelectionWand) {
            this.setSingleItemTool(id -> {
                this.wandItem = id;
                this.wandItemDefault = id.equals(this.config.wandItem);
            }, this.wandItem, item);
        } else if (tool instanceof NavigationWand) {
            this.setSingleItemTool(id -> {
                this.navWandItem = id;
                this.navWandItemDefault = id.equals(this.config.navigationWand);
            }, this.navWandItem, item);
        } else if (tool == null) {
            String id2 = item.id();
            if (id2.equals(this.wandItem)) {
                this.wandItem = null;
                this.setDirty();
            } else if (id2.equals(this.navWandItem)) {
                this.navWandItem = null;
                this.setDirty();
            }
        }
        this.tools.put(item, tool);
    }

    private void setSingleItemTool(Consumer<String> setter, @Nullable String itemId, ItemType newItem) {
        ItemType item;
        if (itemId != null && (item = ItemTypes.get(itemId)) != null) {
            this.tools.remove(item);
        }
        setter.accept(newItem.id());
        this.setDirty();
    }

    public boolean isUsingInventory() {
        return this.useInventory;
    }

    public void setUseInventory(boolean useInventory) {
        this.useInventory = useInventory;
    }

    @Nullable
    public String getLastScript() {
        return this.lastScript;
    }

    public void setLastScript(@Nullable String lastScript) {
        this.lastScript = lastScript;
        this.setDirty();
    }

    public void tellVersion(Actor actor) {
        if (this.hasBeenToldVersion) {
            return;
        }
        this.hasBeenToldVersion = true;
        actor.sendAnnouncements();
    }

    public boolean shouldUseServerCUI() {
        return this.useServerCUI;
    }

    public void setUseServerCUI(boolean useServerCUI) {
        this.useServerCUI = useServerCUI;
        this.setDirty();
    }

    public void updateServerCUI(Actor actor) {
        if (!actor.isPlayer()) {
            return;
        }
        if (!this.config.serverSideCUI) {
            return;
        }
        Player player = (Player)actor;
        if (!this.useServerCUI || this.hasCUISupport) {
            if (this.cuiTemporaryBlock != null) {
                player.sendFakeBlock(this.cuiTemporaryBlock, null);
                this.cuiTemporaryBlock = null;
            }
            return;
        }
        BaseBlock block = ServerCUIHandler.createStructureBlock(player);
        if (block != null) {
            LinCompoundTag tags = Objects.requireNonNull(block.getNbt(), "createStructureBlock should return nbt");
            BlockVector3 tempCuiTemporaryBlock = BlockVector3.at(tags.getTag("x", LinTagType.intTag()).valueAsInt(), tags.getTag("y", LinTagType.intTag()).valueAsInt(), tags.getTag("z", LinTagType.intTag()).valueAsInt());
            if (this.cuiTemporaryBlock != null && !tempCuiTemporaryBlock.equals((Object)this.cuiTemporaryBlock)) {
                player.sendFakeBlock(this.cuiTemporaryBlock, null);
            }
            this.cuiTemporaryBlock = tempCuiTemporaryBlock;
            player.sendFakeBlock(this.cuiTemporaryBlock, block);
        } else if (this.cuiTemporaryBlock != null) {
            player.sendFakeBlock(this.cuiTemporaryBlock, null);
            this.cuiTemporaryBlock = null;
        }
    }

    public void dispatchCUIEvent(Actor actor, CUIEvent event) {
        Preconditions.checkNotNull((Object)actor);
        Preconditions.checkNotNull((Object)event);
        if (this.hasCUISupport) {
            actor.dispatchCUIEvent(event);
        } else if (this.useServerCUI) {
            this.updateServerCUI(actor);
        }
    }

    public void dispatchCUISetup(Actor actor) {
        if (this.selector != null) {
            this.dispatchCUISelection(actor);
        }
    }

    public void dispatchCUISelection(Actor actor) {
        Preconditions.checkNotNull((Object)actor);
        if (!this.hasCUISupport && this.useServerCUI) {
            this.updateServerCUI(actor);
            return;
        }
        RegionSelector regionSelector = this.selector;
        if (regionSelector instanceof CUIRegion) {
            CUIRegion tempSel = (CUIRegion)((Object)regionSelector);
            if (tempSel.getProtocolVersion() > this.cuiVersion) {
                actor.dispatchCUIEvent(new SelectionShapeEvent(tempSel.getLegacyTypeID()));
                tempSel.describeLegacyCUI(this, actor);
            } else {
                actor.dispatchCUIEvent(new SelectionShapeEvent(tempSel.getTypeID()));
                tempSel.describeCUI(this, actor);
            }
        }
    }

    public void describeCUI(Actor actor) {
        Preconditions.checkNotNull((Object)actor);
        if (!this.hasCUISupport) {
            return;
        }
        RegionSelector regionSelector = this.selector;
        if (regionSelector instanceof CUIRegion) {
            CUIRegion tempSel = (CUIRegion)((Object)regionSelector);
            if (tempSel.getProtocolVersion() > this.cuiVersion) {
                tempSel.describeLegacyCUI(this, actor);
            } else {
                tempSel.describeCUI(this, actor);
            }
        }
    }

    public void handleCUIInitializationMessage(String text, Actor actor) {
        Preconditions.checkNotNull((Object)text);
        if (this.hasCUISupport) {
            this.dispatchCUISelection(actor);
            return;
        }
        if (this.failedCuiAttempts > 3) {
            return;
        }
        String[] split = text.split("\\|", 2);
        if (split.length > 1 && split[0].equalsIgnoreCase("v")) {
            int version;
            if (split[1].length() > 4) {
                ++this.failedCuiAttempts;
                return;
            }
            try {
                version = Integer.parseInt(split[1]);
            }
            catch (NumberFormatException e) {
                WorldEdit.logger.warn("Error while reading CUI init message: " + e.getMessage());
                ++this.failedCuiAttempts;
                return;
            }
            this.setCUISupport(true);
            this.setCUIVersion(version);
            this.dispatchCUISelection(actor);
        }
    }

    public boolean hasCUISupport() {
        return this.hasCUISupport;
    }

    public void setCUISupport(boolean support) {
        this.hasCUISupport = support;
    }

    public int getCUIVersion() {
        return this.cuiVersion;
    }

    public void setCUIVersion(int cuiVersion) {
        if (cuiVersion < 0) {
            throw new IllegalArgumentException("CUI protocol version must be non-negative, but '" + cuiVersion + "' was received.");
        }
        this.cuiVersion = cuiVersion;
    }

    @Nullable
    public Calendar detectDate(String input) {
        Preconditions.checkNotNull((Object)input);
        TimeZone tz = TimeZone.getTimeZone(this.getTimeZone());
        Time.setTimeZone(tz);
        Options opt = new Options();
        opt.setNow(Calendar.getInstance(tz));
        Span date = Chronic.parse(input, opt);
        if (date == null) {
            return null;
        }
        return date.getBeginCalendar();
    }

    public EditSession createEditSession(Actor actor) {
        Preconditions.checkNotNull((Object)actor);
        World world = null;
        if (this.hasWorldOverride()) {
            world = this.getWorldOverride();
        } else if (actor instanceof Locatable && ((Locatable)((Object)actor)).getExtent() instanceof World) {
            world = (World)((Locatable)((Object)actor)).getExtent();
        }
        EditSessionBuilder builder = WorldEdit.getInstance().newEditSessionBuilder().world(world).actor(actor).maxBlocks(this.getBlockChangeLimit()).tracing(this.isTracingActions());
        if (actor.isPlayer() && actor instanceof Player) {
            builder.blockBag(this.getBlockBag((Player)actor));
        }
        EditSession editSession = builder.build();
        Request.request().setEditSession(editSession);
        editSession.setMask(this.mask);
        this.prepareEditingExtents(editSession, actor);
        return editSession;
    }

    private void prepareEditingExtents(EditSession editSession, Actor actor) {
        editSession.setSideEffectApplier(this.sideEffectSet);
        editSession.setReorderMode(this.reorderMode);
        if (editSession.getSurvivalExtent() != null) {
            editSession.getSurvivalExtent().setStripNbt(!actor.hasPermission("worldedit.setnbt"));
        }
        editSession.setTickingWatchdog(this.tickingWatchdog);
    }

    public SideEffectSet getSideEffectSet() {
        return this.sideEffectSet;
    }

    public void setSideEffectSet(SideEffectSet sideEffectSet) {
        this.sideEffectSet = sideEffectSet;
    }

    @Deprecated
    public boolean hasFastMode() {
        return !this.sideEffectSet.doesApplyAny();
    }

    @Deprecated
    public void setFastMode(boolean fastMode) {
        this.sideEffectSet = fastMode ? SideEffectSet.none() : SideEffectSet.defaults();
    }

    @Deprecated
    public EditSession.ReorderMode getReorderMode() {
        return this.reorderMode;
    }

    @Deprecated
    public void setReorderMode(EditSession.ReorderMode reorderMode) {
        this.reorderMode = reorderMode;
    }

    public Mask getMask() {
        return this.mask;
    }

    public void setMask(Mask mask) {
        this.mask = mask;
    }

    public String getWandItem() {
        return this.wandItem;
    }

    public boolean isWandItemDefault() {
        if (this.wandItemDefault == null) {
            this.wandItemDefault = Objects.equals(this.wandItem, this.config.wandItem);
            this.setDirty();
        }
        return this.wandItemDefault;
    }

    public String getNavWandItem() {
        return this.navWandItem;
    }

    public boolean isNavWandItemDefault() {
        if (this.navWandItemDefault == null) {
            this.navWandItemDefault = Objects.equals(this.navWandItem, this.config.navigationWand);
            this.setDirty();
        }
        return this.navWandItemDefault;
    }

    public List<Countable<BlockState>> getLastDistribution() {
        return this.lastDistribution == null ? null : Collections.unmodifiableList(this.lastDistribution);
    }

    public void setLastDistribution(List<Countable<BlockState>> dist) {
        this.lastDistribution = dist;
    }

    public void onIdle() {
        this.cuiVersion = -1;
        this.hasCUISupport = false;
        this.failedCuiAttempts = 0;
    }
}

