/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.registry;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.Registry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public final class NamespacedRegistry<V extends Keyed>
extends Registry<V> {
    private static final String MINECRAFT_NAMESPACE = "minecraft";
    private final Set<String> knownNamespaces = new HashSet<String>();
    private final String defaultNamespace;

    public NamespacedRegistry(String name) {
        this(name, MINECRAFT_NAMESPACE);
    }

    public NamespacedRegistry(String name, boolean checkInitialized) {
        this(name, MINECRAFT_NAMESPACE, checkInitialized);
    }

    public NamespacedRegistry(String name, String defaultNamespace) {
        this(name, defaultNamespace, false);
    }

    public NamespacedRegistry(String name, String defaultNamespace, boolean checkInitialized) {
        super(name, checkInitialized);
        this.defaultNamespace = defaultNamespace;
    }

    @Override
    @Nullable
    public V get(String key) {
        return super.get(this.orDefaultNamespace(key));
    }

    @Override
    public V register(String key, V value) {
        Objects.requireNonNull(key, "key");
        int i = key.indexOf(58);
        Preconditions.checkState((i > 0 ? 1 : 0) != 0, (Object)"key is not namespaced");
        V registered = super.register(key, value);
        this.knownNamespaces.add(key.substring(0, i));
        return registered;
    }

    public Set<String> getKnownNamespaces() {
        return Collections.unmodifiableSet(this.knownNamespaces);
    }

    public String getDefaultNamespace() {
        return this.defaultNamespace;
    }

    private String orDefaultNamespace(String key) {
        if (key.indexOf(58) == -1) {
            return this.defaultNamespace + ":" + key;
        }
        return key;
    }
}

