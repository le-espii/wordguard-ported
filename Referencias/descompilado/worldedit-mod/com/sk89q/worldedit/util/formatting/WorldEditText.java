/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.Locale;
import org.enginehub.piston.config.ConfigHolder;
import org.enginehub.piston.config.ConfigRenderer;
import org.enginehub.piston.config.TextConfig;
import org.enginehub.piston.util.TextHelper;

public class WorldEditText {
    private static final ConfigRenderer RENDERER = ConfigRenderer.getInstance();
    public static final ConfigHolder CONFIG_HOLDER = ConfigHolder.create();

    public static Component format(Component component, Locale locale) {
        return WorldEdit.getInstance().getTranslationManager().convertText(RENDERER.render(component, CONFIG_HOLDER), locale);
    }

    public static String reduceToText(Component component, Locale locale) {
        return TextHelper.reduceToText(WorldEditText.format(component, locale));
    }

    private WorldEditText() {
    }

    static {
        CONFIG_HOLDER.getConfig(TextConfig.commandPrefix()).setValue("/");
    }
}

