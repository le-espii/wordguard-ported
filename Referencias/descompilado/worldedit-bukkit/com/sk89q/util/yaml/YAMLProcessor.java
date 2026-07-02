/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.yaml.snakeyaml.DumperOptions
 *  org.yaml.snakeyaml.DumperOptions$LineBreak
 *  org.yaml.snakeyaml.LoaderOptions
 *  org.yaml.snakeyaml.Yaml
 *  org.yaml.snakeyaml.constructor.BaseConstructor
 *  org.yaml.snakeyaml.constructor.SafeConstructor
 *  org.yaml.snakeyaml.nodes.Tag
 *  org.yaml.snakeyaml.reader.UnicodeReader
 *  org.yaml.snakeyaml.representer.Representer
 */
package com.sk89q.util.yaml;

import com.sk89q.util.StringUtil;
import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLNode;
import com.sk89q.util.yaml.YAMLProcessorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

public class YAMLProcessor
extends YAMLNode {
    public static final String LINE_BREAK = DumperOptions.LineBreak.getPlatformLineBreak().getString();
    public static final char COMMENT_CHAR = '#';
    protected final Yaml yaml;
    protected final File file;
    protected String header = null;
    protected YAMLFormat format;
    private final Map<String, String> comments = new HashMap<String, String>();

    public YAMLProcessor(File file, boolean writeDefaults, YAMLFormat format) {
        super(new LinkedHashMap<String, Object>(), writeDefaults);
        this.format = format;
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndent(4);
        dumperOptions.setDefaultFlowStyle(format.getStyle());
        FancyRepresenter representer = new FancyRepresenter();
        representer.setDefaultFlowStyle(format.getStyle());
        LoaderOptions loaderOptions = new LoaderOptions();
        try {
            int yamlAliasLimit = Integer.getInteger("worldedit.yaml.aliasLimit", 50);
            loaderOptions.setMaxAliasesForCollections(yamlAliasLimit);
            int yamlCodePointLimit = Integer.getInteger("worldedit.yaml.codePointLimit", 0x4000000);
            loaderOptions.setCodePointLimit(yamlCodePointLimit);
        }
        catch (NoSuchMethodError noSuchMethodError) {
            // empty catch block
        }
        this.yaml = new Yaml((BaseConstructor)new SafeConstructor(loaderOptions), (Representer)representer, dumperOptions, loaderOptions);
        this.file = file;
    }

    public YAMLProcessor(File file, boolean writeDefaults) {
        this(file, writeDefaults, YAMLFormat.COMPACT);
    }

    public void load() throws IOException {
        try (InputStream stream = this.getInputStream();){
            if (stream == null) {
                throw new IOException("Stream is null!");
            }
            this.read(this.yaml.load((Reader)new UnicodeReader(stream)));
        }
        catch (YAMLProcessorException e) {
            this.root = new LinkedHashMap();
        }
    }

    public void setHeader(String ... headerLines) {
        StringBuilder header = new StringBuilder();
        for (String line : headerLines) {
            if (header.length() > 0) {
                header.append(LINE_BREAK);
            }
            header.append(line);
        }
        this.setHeader(header.toString());
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return this.header;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean save() {
        File parent = this.file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try (OutputStream stream = this.getOutputStream();){
            if (stream == null) {
                boolean bl2 = false;
                return bl2;
            }
            OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
            if (this.header != null) {
                writer.append(this.header);
                writer.append(LINE_BREAK);
            }
            if (this.comments.isEmpty() || this.format != YAMLFormat.EXTENDED) {
                this.yaml.dump((Object)this.root, (Writer)writer);
            } else {
                for (Map.Entry entry : this.root.entrySet()) {
                    String comment = this.comments.get(entry.getKey());
                    if (comment != null) {
                        writer.append(LINE_BREAK);
                        writer.append(comment);
                        writer.append(LINE_BREAK);
                    }
                    this.yaml.dump(Collections.singletonMap((String)entry.getKey(), entry.getValue()), (Writer)writer);
                }
            }
            boolean bl = true;
            return bl;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    private void read(Object input) throws YAMLProcessorException {
        try {
            this.root = null == input ? new LinkedHashMap() : new LinkedHashMap((Map)input);
        }
        catch (ClassCastException e) {
            throw new YAMLProcessorException("Root document must be an key-value structure");
        }
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this.file);
    }

    public String getComment(String key) {
        return this.comments.get(key);
    }

    public void setComment(String key, String comment) {
        if (comment != null) {
            this.setComment(key, comment.split("\\r?\\n"));
        } else {
            this.comments.remove(key);
        }
    }

    public void setComment(String key, String ... comment) {
        if (comment != null && comment.length > 0) {
            for (int i = 0; i < comment.length; ++i) {
                if (comment[i].matches("^# ?")) continue;
                comment[i] = "# " + comment[i];
            }
            String s = StringUtil.joinString(comment, LINE_BREAK);
            this.comments.put(key, s);
        } else {
            this.comments.remove(key);
        }
    }

    public Map<String, String> getComments() {
        return Collections.unmodifiableMap(this.comments);
    }

    public void setComments(Map<String, String> comments) {
        this.comments.clear();
        if (comments != null) {
            this.comments.putAll(comments);
        }
    }

    public static YAMLNode getEmptyNode(boolean writeDefaults) {
        return new YAMLNode(new LinkedHashMap<String, Object>(), writeDefaults);
    }

    private static class FancyRepresenter
    extends Representer {
        private FancyRepresenter() {
            super(new DumperOptions());
            this.nullRepresenter = o -> this.representScalar(Tag.NULL, "");
        }
    }
}

