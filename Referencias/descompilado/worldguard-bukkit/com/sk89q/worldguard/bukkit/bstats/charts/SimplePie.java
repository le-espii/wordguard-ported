/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.bukkit.bstats.charts;

import com.sk89q.worldguard.bukkit.bstats.charts.CustomChart;
import com.sk89q.worldguard.bukkit.bstats.json.JsonObjectBuilder;
import java.util.concurrent.Callable;

public class SimplePie
extends CustomChart {
    private final Callable<String> callable;

    public SimplePie(String chartId, Callable<String> callable) {
        super(chartId);
        this.callable = callable;
    }

    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        String value = this.callable.call();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new JsonObjectBuilder().appendField("value", value).build();
    }
}

