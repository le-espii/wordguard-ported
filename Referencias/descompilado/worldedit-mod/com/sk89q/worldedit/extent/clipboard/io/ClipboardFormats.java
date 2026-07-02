/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class ClipboardFormats {
    private static final Map<String, ClipboardFormat> aliasMap = new HashMap<String, ClipboardFormat>();
    private static final Multimap<String, ClipboardFormat> fileExtensionMap = HashMultimap.create();
    private static final List<ClipboardFormat> registeredFormats = new ArrayList<ClipboardFormat>();

    public static void registerClipboardFormat(ClipboardFormat format) {
        Preconditions.checkNotNull((Object)format);
        for (String key : format.getAliases()) {
            String lowKey = key.toLowerCase(Locale.ROOT);
            ClipboardFormat old = aliasMap.put(lowKey, format);
            if (old == null) continue;
            aliasMap.put(lowKey, old);
            WorldEdit.logger.warn(format.getClass().getName() + " cannot override existing alias '" + lowKey + "' used by " + old.getClass().getName());
        }
        for (String ext : format.getFileExtensions()) {
            String lowExt = ext.toLowerCase(Locale.ROOT);
            fileExtensionMap.put((Object)lowExt, (Object)format);
        }
        registeredFormats.add(format);
    }

    @Nullable
    public static ClipboardFormat findByAlias(String alias) {
        Preconditions.checkNotNull((Object)alias);
        return aliasMap.get(alias.toLowerCase(Locale.ROOT).trim());
    }

    @Nullable
    public static ClipboardFormat findByFile(File file) {
        Preconditions.checkNotNull((Object)file);
        for (ClipboardFormat format : registeredFormats) {
            if (!format.isFormat(file)) continue;
            return format;
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static ClipboardFormat findByInputStream(Supplier<InputStream> inputStreamSupplier) {
        Preconditions.checkNotNull(inputStreamSupplier);
        Iterator<ClipboardFormat> iterator = registeredFormats.iterator();
        while (iterator.hasNext()) {
            ClipboardFormat format = iterator.next();
            try {
                InputStream stream = inputStreamSupplier.get();
                try {
                    if (!format.isFormat(stream)) continue;
                    ClipboardFormat clipboardFormat = format;
                    return clipboardFormat;
                }
                finally {
                    if (stream == null) continue;
                    stream.close();
                }
            }
            catch (IOException iOException) {}
        }
        return null;
    }

    public static Multimap<String, ClipboardFormat> getFileExtensionMap() {
        return Multimaps.unmodifiableMultimap(fileExtensionMap);
    }

    public static Collection<ClipboardFormat> getAll() {
        return Collections.unmodifiableCollection(registeredFormats);
    }

    public static String[] getFileExtensionArray() {
        return fileExtensionMap.keySet().toArray(new String[fileExtensionMap.keySet().size()]);
    }

    private ClipboardFormats() {
    }

    static {
        for (BuiltInClipboardFormat format : BuiltInClipboardFormat.values()) {
            ClipboardFormats.registerClipboardFormat(format);
        }
    }
}

