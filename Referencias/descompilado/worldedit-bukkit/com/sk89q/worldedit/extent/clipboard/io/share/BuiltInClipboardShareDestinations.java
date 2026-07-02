/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.sk89q.worldedit.extent.clipboard.io.share;

import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareDestination;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareMetadata;
import com.sk89q.worldedit.extent.clipboard.io.share.ShareOutputProvider;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.paste.EngineHubPaste;
import com.sk89q.worldedit.util.paste.PasteMetadata;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

public enum BuiltInClipboardShareDestinations implements ClipboardShareDestination
{
    ENGINEHUB_PASTEBIN(new String[]{"enginehub_paste", "ehpaste"}){

        @Override
        public String getName() {
            return "EngineHub Paste";
        }

        @Override
        public Consumer<Actor> share(ClipboardShareMetadata metadata, ShareOutputProvider serializer) throws Exception {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            serializer.writeTo(outputStream);
            PasteMetadata pasteMetadata = new PasteMetadata();
            pasteMetadata.author = metadata.author();
            pasteMetadata.extension = metadata.format().getPrimaryFileExtension();
            pasteMetadata.name = metadata.name();
            EngineHubPaste pasteService = new EngineHubPaste();
            URL url = pasteService.paste(new String(Base64.getEncoder().encode(outputStream.toByteArray()), StandardCharsets.UTF_8), pasteMetadata).call();
            String urlString = url.toExternalForm() + ".schem";
            return actor -> actor.printInfo(TextComponent.of(urlString).clickEvent(ClickEvent.openUrl(urlString)));
        }

        @Override
        public ClipboardFormat getDefaultFormat() {
            return BuiltInClipboardFormat.SPONGE_V2_SCHEMATIC;
        }

        @Override
        public boolean supportsFormat(ClipboardFormat format) {
            return format == this.getDefaultFormat();
        }
    };

    private final ImmutableSet<String> aliases;

    private BuiltInClipboardShareDestinations(String ... aliases) {
        this.aliases = ImmutableSet.copyOf((Object[])aliases);
    }

    public ImmutableSet<String> getAliases() {
        return this.aliases;
    }

    @Override
    public String getName() {
        return this.name();
    }
}

