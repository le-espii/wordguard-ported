/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.config;

import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.HashMap;
import java.util.Map;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.config.Config;
import org.enginehub.piston.config.ConfigRenderer;
import org.enginehub.piston.config.TextConfig;

public class ConfigHolder {
    private final Map<String, Config<?>> configs = new HashMap();

    public static ConfigHolder create() {
        return new ConfigHolder(Config.defaultInstances);
    }

    private ConfigHolder(Map<String, Config<?>> configs) {
        this.configs.putAll(configs);
    }

    public Map<String, Config<?>> getConfigs() {
        return this.configs;
    }

    public <T> Config<T> getConfig(Config<T> defaultValue) {
        Config c = this.configs.computeIfAbsent(defaultValue.getKey(), k -> defaultValue);
        return c;
    }

    public void addConfig(Config<?> config) {
        this.configs.put(config.getKey(), config);
    }

    @Deprecated
    public Component replace(Component input) {
        return ConfigRenderer.getInstance().render(input, this);
    }

    static {
        ColorConfig.mainText();
        TextConfig.commandPrefix();
    }
}

