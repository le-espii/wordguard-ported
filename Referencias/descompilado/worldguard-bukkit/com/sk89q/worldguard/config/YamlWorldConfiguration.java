/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.util.yaml.YAMLProcessor
 *  com.sk89q.worldedit.util.report.Unreported
 */
package com.sk89q.worldguard.config;

import com.sk89q.util.yaml.YAMLProcessor;
import com.sk89q.worldedit.util.report.Unreported;
import com.sk89q.worldguard.config.WorldConfiguration;
import java.util.ArrayList;
import java.util.List;

public abstract class YamlWorldConfiguration
extends WorldConfiguration {
    @Unreported
    protected YAMLProcessor parentConfig;
    @Unreported
    protected YAMLProcessor config;

    public boolean getBoolean(String node, boolean def) {
        boolean val = this.parentConfig.getBoolean(node, def);
        if (this.config.getProperty(node) != null) {
            return this.config.getBoolean(node, def);
        }
        return val;
    }

    public String getString(String node, String def) {
        String val = this.parentConfig.getString(node, def);
        if (this.config.getProperty(node) != null) {
            return this.config.getString(node, def);
        }
        return val;
    }

    public int getInt(String node, int def) {
        int val = this.parentConfig.getInt(node, def);
        if (this.config.getProperty(node) != null) {
            return this.config.getInt(node, def);
        }
        return val;
    }

    private double getDouble(String node, double def) {
        double val = this.parentConfig.getDouble(node, def);
        if (this.config.getProperty(node) != null) {
            return this.config.getDouble(node, def);
        }
        return val;
    }

    public List<Integer> getIntList(String node, List<Integer> def) {
        List res = this.parentConfig.getIntList(node, def);
        if (res == null || res.size() == 0) {
            this.parentConfig.setProperty(node, new ArrayList());
        }
        if (this.config.getProperty(node) != null) {
            res = this.config.getIntList(node, def);
        }
        return res;
    }

    public List<String> getStringList(String node, List<String> def) {
        List res = this.parentConfig.getStringList(node, def);
        if (res == null || res.size() == 0) {
            this.parentConfig.setProperty(node, new ArrayList());
        }
        if (this.config.getProperty(node) != null) {
            res = this.config.getStringList(node, def);
        }
        return res;
    }

    public List<String> getKeys(String node) {
        ArrayList res = this.parentConfig.getKeys(node);
        if (res == null || res.size() == 0) {
            res = this.config.getKeys(node);
        }
        if (res == null) {
            res = new ArrayList();
        }
        return res;
    }

    public Object getProperty(String node) {
        Object res = this.parentConfig.getProperty(node);
        if (this.config.getProperty(node) != null) {
            res = this.config.getProperty(node);
        }
        return res;
    }
}

