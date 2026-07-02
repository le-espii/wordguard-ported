/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.minecraft.class_155
 *  net.minecraft.class_1937
 *  net.minecraft.class_2818
 *  net.minecraft.class_2960
 *  net.minecraft.class_3176
 *  net.minecraft.class_3218
 *  net.minecraft.class_3222
 *  net.minecraft.class_3324
 *  net.minecraft.class_5268
 *  net.minecraft.class_7924
 */
package com.sk89q.worldedit.fabric;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.AbstractPlatform;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.MultiUserPlatform;
import com.sk89q.worldedit.extension.platform.Preference;
import com.sk89q.worldedit.extension.platform.Watchdog;
import com.sk89q.worldedit.fabric.FabricConfiguration;
import com.sk89q.worldedit.fabric.FabricDataFixer;
import com.sk89q.worldedit.fabric.FabricPlayer;
import com.sk89q.worldedit.fabric.FabricRegistries;
import com.sk89q.worldedit.fabric.FabricWorld;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.fabric.internal.ExtendedChunk;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.lifecycle.Lifecycled;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.Registries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_155;
import net.minecraft.class_1937;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_3176;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import net.minecraft.class_5268;
import net.minecraft.class_7924;
import org.enginehub.piston.CommandManager;

class FabricPlatform
extends AbstractPlatform
implements MultiUserPlatform {
    private final FabricWorldEdit mod;
    private final FabricDataFixer dataFixer;
    private final Lifecycled<Optional<Watchdog>> watchdog;
    private boolean hookingEvents = false;
    private static final Set<SideEffect> SUPPORTED_SIDE_EFFECTS_NO_MIXIN = Sets.immutableEnumSet((Enum)SideEffect.VALIDATION, (Enum[])new SideEffect[]{SideEffect.ENTITY_AI, SideEffect.LIGHTING, SideEffect.NEIGHBORS});
    private static final Set<SideEffect> SUPPORTED_SIDE_EFFECTS = Sets.immutableEnumSet((Iterable)Iterables.concat(SUPPORTED_SIDE_EFFECTS_NO_MIXIN, Collections.singleton(SideEffect.UPDATE)));

    FabricPlatform(FabricWorldEdit mod) {
        this.mod = mod;
        this.dataFixer = new FabricDataFixer(this.getDataVersion());
        this.watchdog = FabricWorldEdit.LIFECYCLED_SERVER.map(server -> server instanceof class_3176 ? Optional.of((Watchdog)server) : Optional.empty());
    }

    boolean isHookingEvents() {
        return this.hookingEvents;
    }

    @Override
    public Registries getRegistries() {
        return FabricRegistries.getInstance();
    }

    @Override
    public int getDataVersion() {
        return class_155.method_16673().method_37912().method_38494();
    }

    @Override
    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    @Override
    public boolean isValidMobType(String type) {
        return FabricWorldEdit.getRegistry(class_7924.field_41266).method_10250(class_2960.method_60654((String)type));
    }

    @Override
    public void reload() {
        this.getConfiguration().load();
        super.reload();
    }

    @Override
    public int schedule(long delay, long period, Runnable task) {
        return -1;
    }

    @Override
    @Nullable
    public Watchdog getWatchdog() {
        return this.watchdog.value().flatMap(Function.identity()).orElse(null);
    }

    @Override
    public List<? extends World> getWorlds() {
        Iterable worlds = FabricWorldEdit.LIFECYCLED_SERVER.valueOrThrow().method_3738();
        ArrayList<FabricWorld> ret = new ArrayList<FabricWorld>();
        for (class_3218 world : worlds) {
            ret.add(new FabricWorld((class_1937)world));
        }
        return ret;
    }

    @Override
    @Nullable
    public Player matchPlayer(Player player) {
        if (player instanceof FabricPlayer) {
            return player;
        }
        class_3222 entity = FabricWorldEdit.LIFECYCLED_SERVER.valueOrThrow().method_3760().method_14566(player.getName());
        return entity != null ? new FabricPlayer(entity) : null;
    }

    @Override
    @Nullable
    public World matchWorld(World world) {
        if (world instanceof FabricWorld) {
            return world;
        }
        for (class_3218 ws : FabricWorldEdit.LIFECYCLED_SERVER.valueOrThrow().method_3738()) {
            if (!((class_5268)ws.method_8401()).method_150().equals(world.getName())) continue;
            return new FabricWorld((class_1937)ws);
        }
        return null;
    }

    @Override
    public void registerCommands(CommandManager manager) {
    }

    @Override
    public void setGameHooksEnabled(boolean enabled) {
        this.hookingEvents = enabled;
    }

    @Override
    public FabricConfiguration getConfiguration() {
        return this.mod.getConfig();
    }

    @Override
    public String getVersion() {
        return this.mod.getInternalVersion();
    }

    @Override
    public String getPlatformName() {
        return "Fabric-Official";
    }

    @Override
    public String getPlatformVersion() {
        return this.mod.getInternalVersion();
    }

    @Override
    public String id() {
        return "enginehub:fabric";
    }

    @Override
    public Map<Capability, Preference> getCapabilities() {
        EnumMap<Capability, Preference> capabilities = new EnumMap<Capability, Preference>(Capability.class);
        capabilities.put(Capability.CONFIGURATION, Preference.PREFER_OTHERS);
        capabilities.put(Capability.WORLDEDIT_CUI, Preference.NORMAL);
        capabilities.put(Capability.GAME_HOOKS, Preference.NORMAL);
        capabilities.put(Capability.PERMISSIONS, Preference.NORMAL);
        capabilities.put(Capability.USER_COMMANDS, Preference.NORMAL);
        capabilities.put(Capability.WORLD_EDITING, Preference.PREFERRED);
        return capabilities;
    }

    @Override
    public Set<SideEffect> getSupportedSideEffects() {
        return ExtendedChunk.class.isAssignableFrom(class_2818.class) ? SUPPORTED_SIDE_EFFECTS : SUPPORTED_SIDE_EFFECTS_NO_MIXIN;
    }

    @Override
    public long getTickCount() {
        return FabricWorldEdit.LIFECYCLED_SERVER.valueOrThrow().method_3780();
    }

    @Override
    public Collection<Actor> getConnectedUsers() {
        ArrayList<Actor> users = new ArrayList<Actor>();
        class_3324 scm = FabricWorldEdit.LIFECYCLED_SERVER.valueOrThrow().method_3760();
        for (class_3222 entity : scm.method_14571()) {
            if (entity == null) continue;
            users.add(new FabricPlayer(entity));
        }
        return users;
    }
}

