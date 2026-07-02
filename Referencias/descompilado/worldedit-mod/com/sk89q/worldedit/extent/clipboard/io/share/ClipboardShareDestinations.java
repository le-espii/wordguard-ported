/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent.clipboard.io.share;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.share.BuiltInClipboardShareDestinations;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareDestination;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public class ClipboardShareDestinations {
    private static final Map<String, ClipboardShareDestination> aliasMap = new HashMap<String, ClipboardShareDestination>();
    private static final List<ClipboardShareDestination> registeredDestinations = new ArrayList<ClipboardShareDestination>();

    public static void registerClipboardShareDestination(ClipboardShareDestination destination) {
        Preconditions.checkNotNull((Object)destination);
        Preconditions.checkState((boolean)destination.supportsFormat(destination.getDefaultFormat()), (Object)"Destination must accept its default format");
        for (String key : destination.getAliases()) {
            String lowKey = key.toLowerCase(Locale.ROOT);
            ClipboardShareDestination old = aliasMap.put(lowKey, destination);
            if (old == null) continue;
            aliasMap.put(lowKey, old);
            WorldEdit.logger.warn(destination.getClass().getName() + " cannot override existing alias '" + lowKey + "' used by " + old.getClass().getName());
        }
        registeredDestinations.add(destination);
    }

    @Nullable
    public static ClipboardShareDestination findByAlias(String alias) {
        Preconditions.checkNotNull((Object)alias);
        return aliasMap.get(alias.toLowerCase(Locale.ROOT).trim());
    }

    public static Collection<ClipboardShareDestination> getAll() {
        return Collections.unmodifiableCollection(registeredDestinations);
    }

    private ClipboardShareDestinations() {
    }

    static {
        for (BuiltInClipboardShareDestinations destination : BuiltInClipboardShareDestinations.values()) {
            ClipboardShareDestinations.registerClipboardShareDestination(destination);
        }
    }
}

