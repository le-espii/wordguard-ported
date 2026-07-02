/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.blacklist.logger;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.blacklist.logger.LogFileWriter;
import com.sk89q.worldguard.blacklist.logger.LoggerHandler;
import com.sk89q.worldguard.blacklist.target.Target;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class FileHandler
implements LoggerHandler {
    private static Pattern pattern = Pattern.compile("%.");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int cacheSize = 10;
    private String pathPattern;
    private String worldName;
    private TreeMap<String, LogFileWriter> writers = new TreeMap();
    private final Logger logger;

    public FileHandler(String pathPattern, String worldName, Logger logger) {
        this.pathPattern = pathPattern;
        this.worldName = worldName;
        this.logger = logger;
    }

    public FileHandler(String pathPattern, int cacheSize, String worldName, Logger logger) {
        if (cacheSize < 1) {
            throw new IllegalArgumentException("Cache size cannot be less than 1");
        }
        this.pathPattern = pathPattern;
        this.cacheSize = cacheSize;
        this.worldName = worldName;
        this.logger = logger;
    }

    @Nullable
    private String buildPath(@Nullable String playerName) {
        GregorianCalendar calendar = new GregorianCalendar();
        Matcher m = pattern.matcher(this.pathPattern);
        StringBuilder buffer = new StringBuilder();
        while (m.find()) {
            String group = m.group();
            String rep = "?";
            if (group.matches("%%")) {
                rep = "%";
            } else if (group.matches("%u")) {
                if (playerName == null) {
                    return null;
                }
                rep = playerName.toLowerCase().replaceAll("[^A-Za-z0-9_]", "_");
                if (rep.length() > 32) {
                    rep = rep.substring(0, 32);
                }
            } else if (group.matches("%w")) {
                rep = this.worldName.toLowerCase().replaceAll("[^A-Za-z0-9_]", "_");
                if (rep.length() > 32) {
                    rep = rep.substring(0, 32);
                }
            } else if (group.matches("%Y")) {
                rep = String.valueOf(calendar.get(1));
            } else if (group.matches("%m")) {
                rep = String.format("%02d", calendar.get(2));
            } else if (group.matches("%d")) {
                rep = String.format("%02d", calendar.get(5));
            } else if (group.matches("%W")) {
                rep = String.format("%02d", calendar.get(3));
            } else if (group.matches("%H")) {
                rep = String.format("%02d", calendar.get(11));
            } else if (group.matches("%h")) {
                rep = String.format("%02d", calendar.get(10));
            } else if (group.matches("%i")) {
                rep = String.format("%02d", calendar.get(12));
            } else if (group.matches("%s")) {
                rep = String.format("%02d", calendar.get(13));
            }
            m.appendReplacement(buffer, rep);
        }
        m.appendTail(buffer);
        return buffer.toString();
    }

    private void log(@Nullable LocalPlayer player, String message, String comment) {
        block10: {
            String path = this.buildPath(player != null ? player.getName() : null);
            if (path == null) {
                return;
            }
            try {
                File file;
                File parent;
                String date = dateFormat.format(new Date());
                String line = "[" + date + "] " + (player != null ? player.getName() : "Unknown Source") + ": " + message + (String)(comment != null ? " (" + comment + ")" : "") + "\r\n";
                LogFileWriter writer = this.writers.get(path);
                if (writer != null) {
                    try {
                        BufferedWriter out = writer.getWriter();
                        out.write(line);
                        out.flush();
                        writer.updateLastUse();
                        return;
                    }
                    catch (IOException out) {
                        // empty catch block
                    }
                }
                if ((parent = (file = new File(path)).getParentFile()) != null && !parent.exists()) {
                    parent.mkdirs();
                }
                FileWriter stream = new FileWriter(path, true);
                BufferedWriter out = new BufferedWriter(stream);
                out.write(line);
                out.flush();
                writer = new LogFileWriter(path, out);
                this.writers.put(path, writer);
                if (this.writers.size() <= this.cacheSize) break block10;
                Iterator<Map.Entry<String, LogFileWriter>> it = this.writers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, LogFileWriter> entry = it.next();
                    try {
                        entry.getValue().getWriter().close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    it.remove();
                    if (this.writers.size() > this.cacheSize) continue;
                    break;
                }
            }
            catch (IOException e) {
                this.logger.log(Level.WARNING, "Failed to log blacklist event to '" + path + "': " + e.getMessage());
            }
        }
    }

    private String getCoordinates(BlockVector3 pos) {
        return "@" + pos.x() + "," + pos.y() + "," + pos.z();
    }

    private void logEvent(BlacklistEvent event, String text, Target target, BlockVector3 pos, String comment) {
        this.log(event.getPlayer(), "Tried to " + text + " " + target.getFriendlyName() + " " + this.getCoordinates(pos), comment);
    }

    @Override
    public void logEvent(BlacklistEvent event, String comment) {
        this.logEvent(event, event.getDescription(), event.getTarget(), event.getPosition(), comment);
    }

    @Override
    public void close() {
        for (Map.Entry<String, LogFileWriter> entry : this.writers.entrySet()) {
            try {
                entry.getValue().getWriter().close();
            }
            catch (IOException iOException) {}
        }
        this.writers.clear();
    }
}

