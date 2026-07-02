/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  com.sk89q.worldedit.util.formatting.text.format.TextDecoration
 *  com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer
 *  com.sk89q.worldedit.world.entity.EntityType
 *  com.sk89q.worldedit.world.gamemode.GameMode
 *  com.sk89q.worldedit.world.weather.WeatherType
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.BuildFlag;
import com.sk89q.worldguard.protection.flags.CommandStringFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegistryFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class Flags {
    private static final List<String> INBUILT_FLAGS_LIST = new ArrayList<String>();
    public static final List<String> INBUILT_FLAGS = Collections.unmodifiableList(INBUILT_FLAGS_LIST);
    public static final StateFlag PASSTHROUGH = Flags.register(new StateFlag("passthrough", false));
    public static final SetFlag<String> NONPLAYER_PROTECTION_DOMAINS = Flags.register(new SetFlag<String>("nonplayer-protection-domains", null, new StringFlag(null)));
    public static final StateFlag BUILD = Flags.register(new BuildFlag("build", true));
    public static final StateFlag BLOCK_BREAK = Flags.register(new StateFlag("block-break", false));
    public static final StateFlag BLOCK_PLACE = Flags.register(new StateFlag("block-place", false));
    public static final StateFlag USE = Flags.register(new StateFlag("use", false));
    public static final StateFlag INTERACT = Flags.register(new StateFlag("interact", false));
    public static final StateFlag DAMAGE_ANIMALS = Flags.register(new StateFlag("damage-animals", false));
    public static final StateFlag PVP = Flags.register(new StateFlag("pvp", false));
    public static final StateFlag SLEEP = Flags.register(new StateFlag("sleep", false));
    public static final StateFlag RESPAWN_ANCHORS = Flags.register(new StateFlag("respawn-anchors", false));
    public static final StateFlag TNT = Flags.register(new StateFlag("tnt", false));
    public static final StateFlag CHEST_ACCESS = Flags.register(new StateFlag("chest-access", false));
    public static final StateFlag PLACE_VEHICLE = Flags.register(new StateFlag("vehicle-place", false));
    public static final StateFlag DESTROY_VEHICLE = Flags.register(new StateFlag("vehicle-destroy", false));
    public static final StateFlag LIGHTER = Flags.register(new StateFlag("lighter", false));
    public static final StateFlag RIDE = Flags.register(new StateFlag("ride", false));
    public static final StateFlag POTION_SPLASH = Flags.register(new StateFlag("potion-splash", false));
    public static final StateFlag ITEM_FRAME_ROTATE = Flags.register(new StateFlag("item-frame-rotation", false));
    public static final StateFlag TRAMPLE_BLOCKS = Flags.register(new StateFlag("block-trampling", false));
    public static final StateFlag FIREWORK_DAMAGE = Flags.register(new StateFlag("firework-damage", false));
    public static final StateFlag USE_ANVIL = Flags.register(new StateFlag("use-anvil", false));
    public static final StateFlag USE_DRIPLEAF = Flags.register(new StateFlag("use-dripleaf", false));
    public static final StateFlag WIND_CHARGE_BURST = Flags.register(new StateFlag("wind-charge-burst", false));
    public static final StateFlag ITEM_PICKUP = Flags.register(new StateFlag("item-pickup", true));
    public static final StateFlag ITEM_DROP = Flags.register(new StateFlag("item-drop", true));
    public static final StateFlag EXP_DROPS = Flags.register(new StateFlag("exp-drops", true));
    public static final StateFlag MOB_DAMAGE = Flags.register(new StateFlag("mob-damage", true));
    public static final StateFlag CREEPER_EXPLOSION = Flags.register(new StateFlag("creeper-explosion", true));
    public static final StateFlag ENDERDRAGON_BLOCK_DAMAGE = Flags.register(new StateFlag("enderdragon-block-damage", true));
    public static final StateFlag GHAST_FIREBALL = Flags.register(new StateFlag("ghast-fireball", true));
    public static final StateFlag OTHER_EXPLOSION = Flags.register(new StateFlag("other-explosion", true));
    public static final StateFlag BREEZE_WIND_CHARGE = Flags.register(new StateFlag("breeze-charge-explosion", true));
    public static final StateFlag WITHER_DAMAGE = Flags.register(new StateFlag("wither-damage", true));
    public static final StateFlag ENDER_BUILD = Flags.register(new StateFlag("enderman-grief", true));
    public static final StateFlag SNOWMAN_TRAILS = Flags.register(new StateFlag("snowman-trails", true));
    public static final StateFlag RAVAGER_RAVAGE = Flags.register(new StateFlag("ravager-grief", true));
    public static final StateFlag ENTITY_PAINTING_DESTROY = Flags.register(new StateFlag("entity-painting-destroy", true));
    public static final StateFlag ENTITY_ITEM_FRAME_DESTROY = Flags.register(new StateFlag("entity-item-frame-destroy", true));
    public static final StateFlag MOB_SPAWNING = Flags.register(new StateFlag("mob-spawning", true));
    public static final SetFlag<EntityType> DENY_SPAWN = Flags.register(new SetFlag("deny-spawn", new RegistryFlag(null, EntityType.REGISTRY)));
    public static final StateFlag PISTONS = Flags.register(new StateFlag("pistons", true));
    public static final StateFlag FIRE_SPREAD = Flags.register(new StateFlag("fire-spread", true));
    public static final StateFlag LAVA_FIRE = Flags.register(new StateFlag("lava-fire", true));
    public static final StateFlag LIGHTNING = Flags.register(new StateFlag("lightning", true));
    public static final StateFlag SNOW_FALL = Flags.register(new StateFlag("snow-fall", true));
    public static final StateFlag SNOW_MELT = Flags.register(new StateFlag("snow-melt", true));
    public static final StateFlag ICE_FORM = Flags.register(new StateFlag("ice-form", true));
    public static final StateFlag ICE_MELT = Flags.register(new StateFlag("ice-melt", true));
    public static final StateFlag FROSTED_ICE_MELT = Flags.register(new StateFlag("frosted-ice-melt", true));
    public static final StateFlag FROSTED_ICE_FORM = Flags.register(new StateFlag("frosted-ice-form", false));
    public static final StateFlag MUSHROOMS = Flags.register(new StateFlag("mushroom-growth", true));
    public static final StateFlag LEAF_DECAY = Flags.register(new StateFlag("leaf-decay", true));
    public static final StateFlag GRASS_SPREAD = Flags.register(new StateFlag("grass-growth", true));
    public static final StateFlag MYCELIUM_SPREAD = Flags.register(new StateFlag("mycelium-spread", true));
    public static final StateFlag VINE_GROWTH = Flags.register(new StateFlag("vine-growth", true));
    public static final StateFlag ROCK_GROWTH = Flags.register(new StateFlag("rock-growth", true));
    public static final StateFlag SCULK_GROWTH = Flags.register(new StateFlag("sculk-growth", true));
    public static final StateFlag CROP_GROWTH = Flags.register(new StateFlag("crop-growth", true));
    public static final StateFlag SOIL_DRY = Flags.register(new StateFlag("soil-dry", true));
    public static final StateFlag CORAL_FADE = Flags.register(new StateFlag("coral-fade", true));
    public static final StateFlag COPPER_FADE = Flags.register(new StateFlag("copper-fade", true));
    public static final StateFlag WATER_FLOW = Flags.register(new StateFlag("water-flow", true));
    public static final StateFlag LAVA_FLOW = Flags.register(new StateFlag("lava-flow", true));
    public static final StateFlag MOISTURE_CHANGE = Flags.register(new StateFlag("moisture-change", true));
    public static final RegistryFlag<WeatherType> WEATHER_LOCK = Flags.register(new RegistryFlag("weather-lock", WeatherType.REGISTRY));
    public static final StringFlag TIME_LOCK = Flags.register(new StringFlag("time-lock"));
    public static final StateFlag SEND_CHAT = Flags.register(new StateFlag("send-chat", true));
    public static final StateFlag RECEIVE_CHAT = Flags.register(new StateFlag("receive-chat", true));
    public static final SetFlag<String> BLOCKED_CMDS = Flags.register(new SetFlag<String>("blocked-cmds", new CommandStringFlag(null)));
    public static final SetFlag<String> ALLOWED_CMDS = Flags.register(new SetFlag<String>("allowed-cmds", new CommandStringFlag(null)));
    public static final LocationFlag TELE_LOC = Flags.register(new LocationFlag("teleport"));
    public static final LocationFlag SPAWN_LOC = Flags.register(new LocationFlag("spawn", RegionGroup.MEMBERS));
    @Deprecated
    public static final StringFlag TELE_MESSAGE = Flags.register(new StringFlag("teleport-message", LegacyComponentSerializer.INSTANCE.serialize(TextComponent.of((String)"").append((Component)TextComponent.of((String)"Teleported you to the region '%id%'.", (TextColor)TextColor.LIGHT_PURPLE)))));
    public static final StateFlag INVINCIBILITY = Flags.register(new StateFlag("invincible", false));
    public static final StateFlag FALL_DAMAGE = Flags.register(new StateFlag("fall-damage", true));
    public static final StateFlag HEALTH_REGEN = Flags.register(new StateFlag("natural-health-regen", true));
    public static final StateFlag HUNGER_DRAIN = Flags.register(new StateFlag("natural-hunger-drain", true));
    public static final StateFlag ENTRY = Flags.register(new StateFlag("entry", true, RegionGroup.NON_MEMBERS));
    public static final StateFlag EXIT = Flags.register(new StateFlag("exit", true, RegionGroup.NON_MEMBERS));
    public static final BooleanFlag EXIT_OVERRIDE = Flags.register(new BooleanFlag("exit-override"));
    public static final StateFlag EXIT_VIA_TELEPORT = Flags.register(new StateFlag("exit-via-teleport", true));
    public static final StateFlag ENDERPEARL = Flags.register(new StateFlag("enderpearl", true));
    public static final StateFlag CHORUS_TELEPORT = Flags.register(new StateFlag("chorus-fruit-teleport", true));
    @Deprecated
    public static final StringFlag GREET_MESSAGE = Flags.register(new StringFlag("greeting"));
    @Deprecated
    public static final StringFlag FAREWELL_MESSAGE = Flags.register(new StringFlag("farewell"));
    @Deprecated
    public static final StringFlag GREET_TITLE = Flags.register(new StringFlag("greeting-title"));
    @Deprecated
    public static final StringFlag FAREWELL_TITLE = Flags.register(new StringFlag("farewell-title"));
    public static final BooleanFlag NOTIFY_ENTER = Flags.register(new BooleanFlag("notify-enter"));
    public static final BooleanFlag NOTIFY_LEAVE = Flags.register(new BooleanFlag("notify-leave"));
    public static final RegistryFlag<GameMode> GAME_MODE = Flags.register(new RegistryFlag("game-mode", GameMode.REGISTRY));
    private static final Number[] DELAY_VALUES = new Number[]{0, 1, 5};
    private static final Number[] VITALS_VALUES = new Number[]{0, 5, 10, 20};
    private static final Number[] VITALS_MINS = new Number[]{0, 10};
    private static final Number[] VITALS_MAXS = new Number[]{10, 20};
    public static final IntegerFlag HEAL_DELAY = Flags.register(new IntegerFlag("heal-delay"), f -> f.setSuggestedValues(DELAY_VALUES));
    public static final IntegerFlag HEAL_AMOUNT = Flags.register(new IntegerFlag("heal-amount"), f -> f.setSuggestedValues(VITALS_VALUES));
    public static final DoubleFlag MIN_HEAL = Flags.register(new DoubleFlag("heal-min-health"), f -> f.setSuggestedValues(VITALS_MINS));
    public static final DoubleFlag MAX_HEAL = Flags.register(new DoubleFlag("heal-max-health"), f -> f.setSuggestedValues(VITALS_MAXS));
    public static final IntegerFlag FEED_DELAY = Flags.register(new IntegerFlag("feed-delay"), f -> f.setSuggestedValues(DELAY_VALUES));
    public static final IntegerFlag FEED_AMOUNT = Flags.register(new IntegerFlag("feed-amount"), f -> f.setSuggestedValues(VITALS_VALUES));
    public static final IntegerFlag MIN_FOOD = Flags.register(new IntegerFlag("feed-min-hunger"), f -> f.setSuggestedValues(VITALS_MINS));
    public static final IntegerFlag MAX_FOOD = Flags.register(new IntegerFlag("feed-max-hunger"), f -> f.setSuggestedValues(VITALS_MAXS));
    @Deprecated
    public static final StringFlag DENY_MESSAGE = Flags.register(new StringFlag("deny-message", LegacyComponentSerializer.INSTANCE.serialize(((TextComponent)TextComponent.of((String)"").append((Component)TextComponent.of((String)"Hey!", (TextColor)TextColor.RED, (Set)Sets.newHashSet((Object[])new TextDecoration[]{TextDecoration.BOLD})))).append((Component)TextComponent.of((String)" Sorry, but you can't %what% here.", (TextColor)TextColor.GRAY)))));
    @Deprecated
    public static final StringFlag ENTRY_DENY_MESSAGE = Flags.register(new StringFlag("entry-deny-message", LegacyComponentSerializer.INSTANCE.serialize(((TextComponent)TextComponent.of((String)"").append((Component)TextComponent.of((String)"Hey!", (TextColor)TextColor.RED, (Set)Sets.newHashSet((Object[])new TextDecoration[]{TextDecoration.BOLD})))).append((Component)TextComponent.of((String)" You are not permitted to enter this area.", (TextColor)TextColor.GRAY)))));
    @Deprecated
    public static final StringFlag EXIT_DENY_MESSAGE = Flags.register(new StringFlag("exit-deny-message", LegacyComponentSerializer.INSTANCE.serialize(((TextComponent)TextComponent.of((String)"").append((Component)TextComponent.of((String)"Hey!", (TextColor)TextColor.RED, (Set)Sets.newHashSet((Object[])new TextDecoration[]{TextDecoration.BOLD})))).append((Component)TextComponent.of((String)" You are not permitted to leave this area.", (TextColor)TextColor.GRAY)))));

    private Flags() {
    }

    private static <T extends Flag<?>> T register(T flag) throws FlagConflictException {
        WorldGuard.getInstance().getFlagRegistry().register(flag);
        INBUILT_FLAGS_LIST.add(flag.getName());
        return flag;
    }

    private static <T extends Flag<?>> T register(T flag, Consumer<T> cfg) throws FlagConflictException {
        T f = Flags.register(flag);
        cfg.accept(f);
        return f;
    }

    public static Flag<?> fuzzyMatchFlag(FlagRegistry flagRegistry, String id) {
        String compId = id.replace("-", "");
        for (Flag flag : flagRegistry) {
            if (!flag.getName().replace("-", "").equalsIgnoreCase(compId)) continue;
            return flag;
        }
        return null;
    }

    public static void registerAll() {
    }
}

