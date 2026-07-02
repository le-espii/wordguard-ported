/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.formatting.component.TextComponentProducer
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 */
package com.sk89q.worldguard.util.formatting.component;

import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class Notify
extends TextComponentProducer {
    public Notify(String cause, String description) {
        this.append((Component)TextComponent.of((String)"WG: ", (TextColor)TextColor.GRAY));
        this.append((Component)TextComponent.of((String)cause, (TextColor)TextColor.LIGHT_PURPLE));
        this.append((Component)TextComponent.of((String)description, (TextColor)TextColor.GOLD));
    }
}

