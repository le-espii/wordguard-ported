/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.config;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;

public abstract class Config<T> {
    static final Map<String, Config<?>> defaultInstances = new ConcurrentHashMap();
    private static final Set<String> defaultInstanceCheck = new CopyOnWriteArraySet<String>();
    private final String key;
    @Nullable
    private T value;

    protected Config(String key, @Nullable T defaultValue) {
        this.key = key;
        this.checkValue(defaultValue);
        this.value = defaultValue;
        if (defaultInstanceCheck.add(key)) {
            defaultInstances.put(key, this.copyForDefault());
        }
    }

    protected abstract Config<T> copyForDefault();

    public String getKey() {
        return this.key;
    }

    @Nullable
    public T getValue() {
        return this.value;
    }

    public void setValue(@Nullable T value) {
        this.checkValue(value);
        this.value = value;
    }

    public Config<T> value(@Nullable T value) {
        this.setValue(value);
        return this;
    }

    protected void checkValue(@Nullable T value) {
    }

    public Component value() {
        return this.wrapInternal((List<Component>)ImmutableList.of());
    }

    protected Component wrapInternal(List<Component> args) {
        return TranslatableComponent.of(this.key, args);
    }

    protected abstract Component apply(TranslatableComponent var1);
}

