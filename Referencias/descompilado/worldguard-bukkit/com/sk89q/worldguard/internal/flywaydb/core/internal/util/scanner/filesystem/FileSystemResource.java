/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.filesystem;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FileCopyUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class FileSystemResource
implements Resource,
Comparable<FileSystemResource> {
    private File location;

    public FileSystemResource(String location) {
        this.location = new File(location);
    }

    @Override
    public String getLocation() {
        return StringUtils.replaceAll(this.location.getPath(), "\\", "/");
    }

    @Override
    public String getLocationOnDisk() {
        return this.location.getAbsolutePath();
    }

    @Override
    public String loadAsString(String encoding) {
        try {
            FileInputStream inputStream = new FileInputStream(this.location);
            InputStreamReader reader = new InputStreamReader((InputStream)inputStream, Charset.forName(encoding));
            return FileCopyUtils.copyToString(reader);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load filesystem resource: " + this.location.getPath() + " (encoding: " + encoding + ")", e);
        }
    }

    @Override
    public byte[] loadAsBytes() {
        try {
            FileInputStream inputStream = new FileInputStream(this.location);
            return FileCopyUtils.copyToByteArray(inputStream);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load filesystem resource: " + this.location.getPath(), e);
        }
    }

    @Override
    public String getFilename() {
        return this.location.getName();
    }

    @Override
    public int compareTo(FileSystemResource o) {
        return this.location.compareTo(o.location);
    }
}

