/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Table
 *  com.google.common.collect.Tables
 *  com.google.common.util.concurrent.Futures
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.util.translation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.renderer.TranslatableComponentRenderer;
import com.sk89q.worldedit.util.io.ResourceLoader;
import com.sk89q.worldedit.util.io.file.ArchiveUnpacker;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

public class TranslationManager {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Type STRING_MAP_TYPE = new TypeToken<Map<String, String>>(){}.getType();
    private final TranslatableComponentRenderer<Locale> friendlyComponentRenderer = TranslatableComponentRenderer.from(this::getTranslation);
    private final Table<Locale, String, MessageFormat> translationTable = Tables.newCustomTable(new ConcurrentHashMap(), ConcurrentHashMap::new);
    private final Map<Locale, Future<Void>> loadFutures = new HashMap<Locale, Future<Void>>();
    private final Set<Locale> loadedLocales = Sets.newConcurrentHashSet();
    private final Lock loadLock = new ReentrantLock();
    private Locale defaultLocale = Locale.ENGLISH;
    private final ArchiveUnpacker archiveUnpacker;
    private final ResourceLoader resourceLoader;
    private final Path userProvidedFlatRoot;
    private final Path internalZipRoot;
    @Nullable
    private Path userProvidedZipRoot;

    public static String makeTranslationKey(String type, String id) {
        String[] parts = id.split(":", 2);
        return type + "." + parts[0] + "." + parts[1].replace('/', '.');
    }

    public TranslationManager(ArchiveUnpacker archiveUnpacker, ResourceLoader resourceLoader) throws IOException {
        this.archiveUnpacker = archiveUnpacker;
        this.resourceLoader = resourceLoader;
        Preconditions.checkNotNull((Object)resourceLoader);
        this.userProvidedFlatRoot = resourceLoader.getLocalResource("lang");
        this.internalZipRoot = archiveUnpacker.unpackArchive((URL)Preconditions.checkNotNull((Object)resourceLoader.getRootResource("lang/i18n.zip"), (Object)"Missing internal i18n.zip!"));
    }

    private void load() throws IOException {
        Path userZip = this.resourceLoader.getLocalResource("lang/i18n.zip");
        Path result = null;
        if (Files.exists(userZip, new LinkOption[0])) {
            result = this.archiveUnpacker.unpackArchive(userZip.toUri().toURL());
        }
        this.userProvidedZipRoot = result;
    }

    public void reload() {
        this.loadLock.lock();
        try {
            this.loadedLocales.clear();
            for (Future<Void> future : this.loadFutures.values()) {
                Futures.getUnchecked(future);
            }
            this.loadFutures.clear();
            this.translationTable.clear();
            this.load();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
            this.loadLock.unlock();
        }
    }

    public Component convertText(Component component, Locale locale) {
        return this.friendlyComponentRenderer.render(component, locale);
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    private MessageFormat getTranslation(Locale locale, String key) {
        MessageFormat format;
        if (!this.loadedLocales.contains(locale)) {
            this.loadLocale(locale);
        }
        if ((format = (MessageFormat)this.translationTable.get((Object)locale, (Object)key)) == null && !this.defaultLocale.equals(locale)) {
            if (!locale.getCountry().isEmpty()) {
                return this.getTranslation(Locale.of((String)locale.getLanguage()), key);
            }
            return this.getTranslation(this.defaultLocale, key);
        }
        return format;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadLocale(Locale locale) {
        CompletableFuture<Object> ourFuture;
        block9: {
            this.loadLock.lock();
            try {
                Future<Void> ftr = this.loadFutures.get(locale);
                if (ftr == null) {
                    ourFuture = new CompletableFuture<Object>();
                    this.loadFutures.put(locale, ourFuture);
                    break block9;
                }
                Futures.getUnchecked(ftr);
                return;
            }
            finally {
                this.loadLock.unlock();
            }
        }
        try {
            this.loadTranslations(locale);
        }
        catch (Exception t) {
            LOGGER.warn("Failed to load translations, locale=" + String.valueOf(locale), (Throwable)t);
        }
        finally {
            ourFuture.complete(null);
            this.loadedLocales.add(locale);
        }
    }

    private void loadTranslations(Locale locale) throws IOException {
        HashMap<String, String> entries = new HashMap<String, String>();
        String localePath = this.getLocalePath(locale);
        if (this.defaultLocale.equals(locale)) {
            URL internalStrings = this.resourceLoader.getRootResource("lang/strings.json");
            Preconditions.checkNotNull((Object)internalStrings, (Object)"Failed to load internal strings.json");
            try (InputStream in = internalStrings.openStream();){
                this.putTranslationData(entries, in);
            }
        } else {
            this.putTranslationData(entries, this.internalZipRoot.resolve(localePath));
        }
        if (this.userProvidedZipRoot != null) {
            this.putTranslationData(entries, this.userProvidedZipRoot.resolve(localePath));
        }
        this.putTranslationData(entries, this.userProvidedFlatRoot.resolve(localePath));
        for (Map.Entry entry : entries.entrySet()) {
            MessageFormat format;
            try {
                format = new MessageFormat(((String)entry.getValue()).replace("'", "''"), locale);
            }
            catch (IllegalArgumentException e) {
                LOGGER.warn("Failed to load translation, locale=" + String.valueOf(locale) + ", key=" + (String)entry.getKey() + ", value=" + (String)entry.getValue(), (Throwable)e);
                continue;
            }
            this.translationTable.put((Object)locale, (Object)((String)entry.getKey()), (Object)format);
        }
    }

    private String getLocalePath(Locale locale) {
        if (this.defaultLocale.equals(locale)) {
            return "strings.json";
        }
        String country = locale.getCountry().isEmpty() ? "" : "-" + locale.getCountry();
        return locale.getLanguage() + country + "/strings.json";
    }

    private void putTranslationData(Map<String, String> data, Path source) throws IOException {
        if (!Files.exists(source, new LinkOption[0])) {
            return;
        }
        try (InputStream in = Files.newInputStream(source, new OpenOption[0]);){
            this.putTranslationData(data, in);
        }
    }

    private void putTranslationData(Map<String, String> data, InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
            Map map = (Map)GSON.fromJson((Reader)reader, STRING_MAP_TYPE);
            for (Map.Entry entry : map.entrySet()) {
                if (((String)entry.getValue()).isEmpty()) continue;
                data.put((String)entry.getKey(), (String)entry.getValue());
            }
        }
    }
}

