/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SideEffectBox
extends PaginationBox {
    private static final LazyReference<List<SideEffect>> SIDE_EFFECTS = LazyReference.from(() -> WorldEdit.getInstance().getPlatformManager().getSupportedSideEffects().stream().filter(SideEffect::isExposed).sorted(Comparator.comparing(effect -> WorldEditText.reduceToText(TranslatableComponent.of(effect.getDisplayName()), Locale.US))).toList());
    private final SideEffectSet sideEffectSet;
    private static final SideEffect.State[] SHOWN_VALUES = new SideEffect.State[]{SideEffect.State.OFF, SideEffect.State.ON};

    private static List<SideEffect> getSideEffects() {
        return SIDE_EFFECTS.getValue();
    }

    public SideEffectBox(SideEffectSet sideEffectSet) {
        super("Side Effects");
        this.sideEffectSet = sideEffectSet;
    }

    @Override
    public Component getComponent(int number) {
        SideEffect effect = SideEffectBox.getSideEffects().get(number);
        SideEffect.State state = this.sideEffectSet.getState(effect);
        TextComponent.Builder builder = TextComponent.builder();
        builder = (TextComponent.Builder)builder.append(TranslatableComponent.of(effect.getDisplayName(), TextColor.YELLOW).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TranslatableComponent.of(effect.getDescription()))));
        for (SideEffect.State uiState : SHOWN_VALUES) {
            builder = (TextComponent.Builder)builder.append((Component)TextComponent.space());
            builder = (TextComponent.Builder)builder.append(((TranslatableComponent)TranslatableComponent.of(uiState.getDisplayName(), uiState == state ? TextColor.WHITE : TextColor.GRAY).clickEvent(ClickEvent.runCommand("//perf -h " + effect.name().toLowerCase(Locale.US) + " " + uiState.name().toLowerCase(Locale.US)))).hoverEvent(HoverEvent.showText(uiState == state ? TranslatableComponent.of("worldedit.sideeffect.box.current") : TranslatableComponent.of("worldedit.sideeffect.box.change-to", TranslatableComponent.of(uiState.getDisplayName())))));
        }
        return builder.build();
    }

    @Override
    public int getComponentsSize() {
        return SideEffectBox.getSideEffects().size();
    }
}

