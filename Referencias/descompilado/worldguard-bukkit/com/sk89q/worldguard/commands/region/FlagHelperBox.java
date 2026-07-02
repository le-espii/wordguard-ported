/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.UnmodifiableIterator
 *  com.sk89q.worldedit.registry.Keyed
 *  com.sk89q.worldedit.registry.Registry
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.util.formatting.component.PaginationBox
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.TextComponent$Builder
 *  com.sk89q.worldedit.util.formatting.text.TranslatableComponent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  com.sk89q.worldedit.util.formatting.text.format.TextDecoration
 *  com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.commands.region;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.Registry;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.NumberFlag;
import com.sk89q.worldguard.protection.flags.RegistryFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.UnknownFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

class FlagHelperBox
extends PaginationBox {
    private static final List<Flag<?>> FLAGS = WorldGuard.getInstance().getFlagRegistry().getAll().stream().sorted((f1, f2) -> {
        if (f1 == f2) {
            return 0;
        }
        int idx1 = Flags.INBUILT_FLAGS.indexOf(f1.getName());
        int idx2 = Flags.INBUILT_FLAGS.indexOf(f2.getName());
        if (idx1 < 0 && idx2 >= 0) {
            return 1;
        }
        if (idx2 < 0 && idx1 >= 0) {
            return -1;
        }
        if (idx1 < 0) {
            return f1.getName().compareTo(f2.getName());
        }
        return idx1 < idx2 ? -1 : 1;
    }).collect(Collectors.toList());
    private static final int SIZE = FLAGS.size() == Flags.INBUILT_FLAGS.size() ? FLAGS.size() : FLAGS.size() + 1;
    private static final int PAD_PX_SIZE = 180;
    static final Set<Flag<?>> DANGER_ZONE = ImmutableSet.of((Object)Flags.BUILD, (Object)Flags.PASSTHROUGH, (Object)Flags.BLOCK_PLACE, (Object)Flags.BLOCK_BREAK);
    private final World world;
    private final ProtectedRegion region;
    private final RegionPermissionModel perms;
    private boolean monoSpace;

    FlagHelperBox(World world, ProtectedRegion region, RegionPermissionModel perms) {
        super("Flags for " + region.getId(), "/rg flags -w \"" + world.getName() + "\" -p %page% " + region.getId());
        this.world = world;
        this.region = region;
        this.perms = perms;
    }

    public Component getComponent(int number) {
        if (number == Flags.INBUILT_FLAGS.size()) {
            return this.centerAndBorder(TextComponent.of((String)"Third-Party Flags", (TextColor)TextColor.AQUA));
        }
        if (number > Flags.INBUILT_FLAGS.size()) {
            --number;
        }
        Flag<?> flag = FLAGS.get(number);
        return this.createLine(flag, number >= Flags.INBUILT_FLAGS.size());
    }

    public int getComponentsSize() {
        return SIZE;
    }

    private Component createLine(Flag<?> flag, boolean thirdParty) {
        TextComponent.Builder builder = TextComponent.builder((String)"");
        this.appendFlagName(builder, flag, flag instanceof UnknownFlag ? TextColor.GRAY : (thirdParty ? TextColor.LIGHT_PURPLE : TextColor.GOLD));
        this.appendFlagValue(builder, flag);
        return builder.build();
    }

    private void appendFlagName(TextComponent.Builder builder, Flag<?> flag, TextColor color) {
        String name = flag.getName();
        int length = this.monoSpace ? name.length() : FlagFontInfo.getPxLength(name);
        builder.append((Component)TextComponent.of((String)name, (TextColor)color));
        if (flag.usesMembershipAsDefault()) {
            builder.append(((TextComponent)TextComponent.empty().append((Component)TextComponent.of((String)"*", (TextColor)TextColor.AQUA))).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"This is a special flag which defaults to allow for members, and deny for non-members"))));
            length += this.monoSpace ? 1 : FlagFontInfo.getPxLength('*');
        }
        if (flag == Flags.PASSTHROUGH) {
            builder.append(((TextComponent)TextComponent.empty().append((Component)TextComponent.of((String)"*", (TextColor)TextColor.AQUA))).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"This is a special flag which overrides build checks. (Not movement related!)"))));
            length += this.monoSpace ? 1 : FlagFontInfo.getPxLength('*');
        }
        int leftover = (this.monoSpace ? 60 : 180) - length;
        builder.append((Component)TextComponent.space());
        if ((leftover -= 4) > 0) {
            builder.append((Component)TextComponent.of((String)Strings.repeat((String)".", (int)(this.monoSpace ? leftover : leftover / 2)), (TextColor)TextColor.DARK_GRAY));
        }
    }

    private void appendFlagValue(TextComponent.Builder builder, Flag<?> flag) {
        if (flag instanceof StateFlag) {
            this.appendStateFlagValue(builder, (StateFlag)flag);
        } else if (flag instanceof BooleanFlag) {
            this.appendBoolFlagValue(builder, (BooleanFlag)flag);
        } else if (flag instanceof SetFlag) {
            this.appendSetFlagValue(builder, (SetFlag)flag);
        } else if (flag instanceof RegistryFlag) {
            this.appendRegistryFlagValue(builder, (RegistryFlag)flag);
        } else if (flag instanceof StringFlag) {
            this.appendStringFlagValue(builder, (StringFlag)flag);
        } else if (flag instanceof LocationFlag) {
            this.appendLocationFlagValue(builder, (LocationFlag)flag);
        } else if (flag instanceof NumberFlag) {
            this.appendNumericFlagValue(builder, (NumberFlag)flag);
        } else {
            Object display = String.valueOf(this.region.getFlag(flag));
            if (((String)display).length() > 23) {
                display = ((String)display).substring(0, 20) + "...";
            }
            this.appendValueText(builder, flag, (String)display, null);
        }
    }

    private <T> T getInheritedValue(ProtectedRegion region, Flag<T> flag) {
        for (ProtectedRegion parent = region.getParent(); parent != null; parent = parent.getParent()) {
            Object val = parent.getFlag(flag);
            if (val == null) continue;
            return (T)val;
        }
        return null;
    }

    private <V> void appendValueChoices(TextComponent.Builder builder, Flag<V> flag, Iterator<V> choices, @Nullable String suggestChoice) {
        V defVal = flag.getDefault();
        Object currVal = this.region.getFlag(flag);
        boolean inherited = false;
        if (currVal == null && (currVal = this.getInheritedValue(this.region, flag)) != null) {
            inherited = true;
        }
        while (choices.hasNext()) {
            Component valType;
            V choice = choices.next();
            boolean isExplicitSet = currVal == choice && !inherited;
            boolean maySet = this.perms.maySetFlag(this.region, flag, isExplicitSet ? null : String.valueOf(choice));
            TextColor col = isExplicitSet ? TextColor.WHITE : (inherited && currVal == choice ? TextColor.GRAY : TextColor.DARK_GRAY);
            ImmutableSet styles = choice == defVal ? ImmutableSet.of((Object)TextDecoration.UNDERLINED) : Collections.emptySet();
            Component choiceComponent = TextComponent.empty().append((Component)TextComponent.of((String)this.capitalize(String.valueOf(choice)), (TextColor)col, (Set)styles));
            ArrayList<Object> hoverTexts = new ArrayList<Object>();
            if (maySet) {
                if (isExplicitSet) {
                    hoverTexts.add(TextComponent.of((String)"Click to unset", (TextColor)TextColor.GOLD));
                } else if (DANGER_ZONE.contains(flag) && (!"__global__".equals(this.region.getId()) || flag != Flags.PASSTHROUGH)) {
                    hoverTexts.add(((TextComponent)TextComponent.of((String)"Setting this flag may have unintended consequences.", (TextColor)TextColor.RED).append((Component)TextComponent.newline())).append(((TextComponent)TextComponent.of((String)"Please read the documentation and set this flag manually if you really intend to.").append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)"(Hint: You do not need to set this to protect the region!)"))));
                } else {
                    hoverTexts.add(TextComponent.of((String)"Click to set", (TextColor)TextColor.GOLD));
                }
            }
            if ((valType = this.getToolTipHint(defVal, choice, inherited)) != null) {
                hoverTexts.add(valType);
            }
            if (!hoverTexts.isEmpty()) {
                TextComponent.Builder hoverBuilder = TextComponent.builder((String)"");
                Iterator hovIt = hoverTexts.iterator();
                while (hovIt.hasNext()) {
                    hoverBuilder.append((Component)hovIt.next());
                    if (!hovIt.hasNext()) continue;
                    hoverBuilder.append((Component)TextComponent.newline());
                }
                choiceComponent = choiceComponent.hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)hoverBuilder.build()));
            }
            if (maySet && (isExplicitSet || !DANGER_ZONE.contains(flag))) {
                builder.append(choiceComponent.clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)this.makeCommand(flag, isExplicitSet ? "" : choice))));
            } else {
                builder.append(choiceComponent);
            }
            builder.append((Component)TextComponent.space());
        }
        if (suggestChoice != null && this.perms.maySetFlag(this.region, flag)) {
            builder.append(((TextComponent)TextComponent.of((String)suggestChoice, (TextColor)TextColor.DARK_GRAY).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)"Click to set custom value", (TextColor)TextColor.GOLD)))).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)this.makeCommand(flag, ""))));
        }
    }

    private <V> void appendValueText(TextComponent.Builder builder, Flag<V> flag, String display, @Nullable Component hover) {
        Component valType;
        boolean maySet;
        V defVal = flag.getDefault();
        Object currVal = this.region.getFlag(flag);
        boolean inherited = false;
        if (currVal == null && (currVal = this.getInheritedValue(this.region, flag)) != null) {
            inherited = true;
        }
        boolean isExplicitSet = currVal != null && !inherited;
        boolean bl = maySet = !(flag instanceof UnknownFlag) && this.perms.maySetFlag(this.region, flag);
        TextColor col = isExplicitSet ? TextColor.WHITE : (inherited ? TextColor.GRAY : TextColor.DARK_GRAY);
        ImmutableSet styles = currVal == defVal ? ImmutableSet.of((Object)TextDecoration.UNDERLINED) : Collections.emptySet();
        Component displayComponent = TextComponent.empty().append((Component)TextComponent.of((String)display, (TextColor)col, (Set)styles));
        ArrayList<Object> hoverTexts = new ArrayList<Object>();
        if (maySet) {
            if (isExplicitSet) {
                hoverTexts.add(TextComponent.of((String)"Click to change", (TextColor)TextColor.GOLD));
            } else {
                hoverTexts.add(TextComponent.of((String)"Click to set", (TextColor)TextColor.GOLD));
            }
        }
        if ((valType = this.getToolTipHint(defVal, currVal, inherited)) != null) {
            hoverTexts.add(valType);
        }
        if (!hoverTexts.isEmpty()) {
            TextComponent.Builder hoverBuilder = TextComponent.builder((String)"");
            Iterator hovIt = hoverTexts.iterator();
            while (hovIt.hasNext()) {
                hoverBuilder.append((Component)hovIt.next());
                if (!hovIt.hasNext()) continue;
                hoverBuilder.append((Component)TextComponent.newline());
            }
            if (hover != null) {
                hoverBuilder.append((Component)TextComponent.newline());
                hoverBuilder.append(hover);
            }
            displayComponent = displayComponent.hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)hoverBuilder.build()));
        }
        if (maySet) {
            builder.append(displayComponent.clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)this.makeCommand(flag, ""))));
        } else {
            builder.append(displayComponent);
        }
        builder.append((Component)TextComponent.space());
    }

    private String makeCommand(Flag<?> flag, Object choice) {
        return "/rg flag -w \"" + this.world.getName() + "\" -h " + this.getCurrentPage() + " " + this.region.getId() + " " + flag.getName() + " " + String.valueOf(choice);
    }

    private String capitalize(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return (value = value.toLowerCase()).length() > 1 ? Character.toUpperCase(value.charAt(0)) + value.substring(1) : String.valueOf(Character.toUpperCase(value.charAt(0)));
    }

    @Nullable
    private <V> Component getToolTipHint(V defVal, V currVal, boolean inherited) {
        Object valType = inherited ? (currVal == defVal ? ((TextComponent)TextComponent.of((String)"Inherited & ").append(TextComponent.of((String)"default").decoration(TextDecoration.UNDERLINED, true))).append((Component)TextComponent.of((String)" value")) : TextComponent.of((String)"Inherited value")) : (currVal == defVal ? ((TextComponent)TextComponent.empty().append(TextComponent.of((String)"Default").decoration(TextDecoration.UNDERLINED, true))).append((Component)TextComponent.of((String)" value")) : null);
        return valType;
    }

    private void appendStateFlagValue(TextComponent.Builder builder, StateFlag flag) {
        UnmodifiableIterator choices = Iterators.forArray((Object[])StateFlag.State.values());
        this.appendValueChoices(builder, flag, (Iterator)choices, null);
    }

    private void appendBoolFlagValue(TextComponent.Builder builder, BooleanFlag flag) {
        UnmodifiableIterator choices = Iterators.forArray((Object[])new Boolean[]{Boolean.TRUE, Boolean.FALSE});
        this.appendValueChoices(builder, flag, (Iterator)choices, null);
    }

    private <V> void appendSetFlagValue(TextComponent.Builder builder, SetFlag<V> flag) {
        Flag<V> subType = flag.getType();
        Class<RegistryFlag> clazz = subType.getClass();
        String subName = clazz.isAssignableFrom(RegistryFlag.class) ? ((RegistryFlag)subType).getRegistry().getName() : subType.getClass().getSimpleName().replace("Flag", "");
        Set currVal = (Set)this.region.getFlag(flag);
        if (currVal == null) {
            currVal = (Set)this.getInheritedValue(this.region, flag);
        }
        String display = (String)(currVal == null ? "" : currVal.size() + "x ") + subName;
        String stringValue = currVal == null ? "" : currVal.stream().map(String::valueOf).collect(Collectors.joining(","));
        TextComponent hoverComp = TextComponent.of((String)"");
        if (currVal != null) {
            hoverComp = (TextComponent)((TextComponent)((TextComponent)hoverComp.append((Component)TextComponent.of((String)"Current values:"))).append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)stringValue));
        }
        this.appendValueText(builder, flag, display, (Component)hoverComp);
    }

    private void appendRegistryFlagValue(TextComponent.Builder builder, RegistryFlag<?> flag) {
        Registry<?> registry = flag.getRegistry();
        String regName = registry.getName();
        Keyed currVal = (Keyed)this.region.getFlag(flag);
        if (currVal == null) {
            currVal = (Keyed)this.getInheritedValue(this.region, flag);
        }
        String display = currVal == null ? regName : currVal.id();
        this.appendValueText(builder, flag, display, null);
    }

    private void appendLocationFlagValue(TextComponent.Builder builder, LocationFlag flag) {
        Location currVal = (Location)this.region.getFlag(flag);
        if (currVal == null) {
            currVal = this.getInheritedValue(this.region, flag);
        }
        if (currVal == null) {
            Location defVal = (Location)flag.getDefault();
            if (defVal == null) {
                this.appendValueText(builder, flag, "unset location", null);
            } else {
                this.appendValueText(builder, flag, defVal.toString(), ((TextComponent)TextComponent.of((String)"Default value:").append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)defVal.toString())));
            }
        } else {
            this.appendValueText(builder, flag, currVal.toString(), ((TextComponent)TextComponent.of((String)"Current value:").append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)currVal.toString())));
        }
    }

    private <V extends Number> void appendNumericFlagValue(TextComponent.Builder builder, NumberFlag<V> flag) {
        Number currVal = (Number)this.region.getFlag(flag);
        if (currVal == null) {
            currVal = (Number)this.getInheritedValue(this.region, flag);
        }
        Number defVal = (Number)flag.getDefault();
        Number[] suggested = flag.getSuggestedValues();
        TreeSet<Number> choices = new TreeSet<Number>(Comparator.comparing(Number::doubleValue));
        if (currVal != null) {
            choices.add(currVal);
        }
        if (defVal != null) {
            choices.add(defVal);
        }
        if (suggested.length > 0) {
            choices.addAll(Arrays.asList(suggested));
        }
        this.appendValueChoices(builder, flag, choices.iterator(), choices.isEmpty() ? "unset number" : "[custom]");
    }

    private void appendStringFlagValue(TextComponent.Builder builder, StringFlag flag) {
        String currVal = (String)this.region.getFlag(flag);
        if (currVal == null) {
            currVal = this.getInheritedValue(this.region, flag);
        }
        if (currVal == null) {
            String defVal = flag.getDefault();
            if (defVal == null) {
                this.appendValueText(builder, flag, "unset string", null);
            } else {
                TextComponent defComp = LegacyComponentSerializer.INSTANCE.deserialize(defVal);
                Object display = FlagHelperBox.reduceToText((Component)defComp);
                if (((String)(display = ((String)display).replace("\n", "\\n"))).length() > 23) {
                    display = ((String)display).substring(0, 20) + "...";
                }
                this.appendValueText(builder, flag, (String)display, ((TextComponent)TextComponent.of((String)"Default value:").append((Component)TextComponent.newline())).append((Component)defComp));
            }
        } else {
            TextComponent currComp = LegacyComponentSerializer.INSTANCE.deserialize(currVal);
            Object display = FlagHelperBox.reduceToText((Component)currComp);
            if (((String)(display = ((String)display).replace("\n", "\\n"))).length() > 23) {
                display = ((String)display).substring(0, 20) + "...";
            }
            this.appendValueText(builder, flag, (String)display, ((TextComponent)TextComponent.of((String)"Current value:").append((Component)TextComponent.newline())).append((Component)currComp));
        }
    }

    private static String reduceToText(Component component) {
        StringBuilder text = new StringBuilder();
        FlagHelperBox.appendTextTo(text, component);
        return text.toString();
    }

    private static void appendTextTo(StringBuilder builder, Component component) {
        if (component instanceof TextComponent) {
            builder.append(((TextComponent)component).content());
        } else if (component instanceof TranslatableComponent) {
            builder.append(((TranslatableComponent)component).key());
        }
        for (Component child : component.children()) {
            FlagHelperBox.appendTextTo(builder, child);
        }
    }

    void tryMonoSpacing() {
        this.monoSpace = true;
    }

    private static final class FlagFontInfo {
        private FlagFontInfo() {
        }

        static int getPxLength(char c) {
            switch (c) {
                case ':': 
                case 'i': {
                    return 1;
                }
                case 'l': {
                    return 2;
                }
                case '*': 
                case 't': {
                    return 3;
                }
                case 'f': 
                case 'k': {
                    return 4;
                }
            }
            return 5;
        }

        static int getPxLength(String string) {
            return string.chars().reduce(0, (p, i) -> p + FlagFontInfo.getPxLength((char)i) + 1);
        }
    }
}

